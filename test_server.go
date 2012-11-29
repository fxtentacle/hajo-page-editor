package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"net/http"
	"sync"
	"time"
)

type Attachment struct {
	Filename string
	Data     []byte
}

type WebGuiData struct {
	mutex       sync.RWMutex
	attachments map[string]*Attachment
}

type WebGuiUploader WebGuiData
type WebGuiDownloader WebGuiData

func (ogui *WebGuiUploader) ServeHTTP(w http.ResponseWriter, req *http.Request) {
	ogui.mutex.Lock()
	defer ogui.mutex.Unlock()

	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
	w.Header().Add("Access-Control-Allow-Headers", "Origin, X-HTTP-Method-Override, Content-Type, Accept")
	w.Header().Add("Access-Control-Max-Age", "86400")

	if req.Method == "OPTIONS" {
		w.Header().Add("Cache-control", "max-age=86400")
		w.WriteHeader(http.StatusOK)
		return
	}

	ClientID := req.FormValue("ClientID")
	fn := req.FormValue("Filename")
	file, _, err := req.FormFile("Filedata")
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	filedata, err := ioutil.ReadAll(file)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	ogui.attachments[ClientID] = &Attachment{fn, filedata}
	fmt.Println("Stored file", fn, "len=", len(filedata), "as id", ClientID)

	w.WriteHeader(http.StatusOK)
	w.Write([]byte("OK\r\n"))
}

func (ogui *WebGuiDownloader) ServeHTTP(w http.ResponseWriter, req *http.Request) {
	ogui.mutex.Lock()
	defer ogui.mutex.Unlock()

	w.Header().Add("Access-Control-Allow-Origin", "*")
	w.Header().Add("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
	w.Header().Add("Access-Control-Allow-Headers", "Origin, X-HTTP-Method-Override, Content-Type, Accept")
	w.Header().Add("Access-Control-Max-Age", "86400")

	if req.Method == "OPTIONS" {
		w.Header().Add("Cache-control", "max-age=86400")
		w.WriteHeader(http.StatusOK)
		return
	}

	ClientID := req.URL.Query().Get("ClientID")
	file, ok := ogui.attachments[ClientID]
	if !ok {
		fmt.Println("Unknown file", ClientID, "requested")
		http.Error(w, "File not found", http.StatusNotFound)
		return
	}
	fmt.Println("Found file", file.Filename, "len=", len(file.Data), "as id", ClientID)

	http.ServeContent(w, req, file.Filename, time.Now(), bytes.NewReader(file.Data))
}

func main() {
	data := &WebGuiData{attachments: make(map[string]*Attachment)}
	http.Handle("/upload", (*WebGuiUploader)(data))
	http.Handle("/download", (*WebGuiDownloader)(data))

	http.ListenAndServe(":8080", nil)
}
