Hajo's Page Editor
==================

This is a Web Page editor written in JavaScript. The HTML it generates has all styles inlined and uses no absolute positioning. That should make this editor suitable for HTML email.

Getting Started
---------------

The `hajo_war.zip` includes a complete version of the JavaScript, CSS and FontAwesome alongside with an example html page. You can host it somewhere to try out the JS editor, but image upload and saving won't work... To make the images work, `hajo_server.zip` includes a darwin-amd64 binary of my testing server.

If you prefer to compile your own stuff, extract GWT 2.5.0 into the project folder and then compile it with eclipse. The test server is written in Google Go and resides in `test_server.go`

