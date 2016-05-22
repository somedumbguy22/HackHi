/** * Take picture with camera */
function takePicture() 
{    
	navigator.camera.getPicture(function(uri) 
		{            
			var img = document.getElementById('camera_image');            
			img.style.visibility = "visible";            
			img.style.display = "block";            
			img.src = uri;            
			document.getElementById('camera_status').innerHTML = "Success";        
		},  function(e) 
		{            
			console.log("Error getting picture: " + e);            
			document.getElementById('camera_status').innerHTML = "Error getting picture.";        
		},        
		{ quality: 50, destinationType: navigator.camera.DestinationType.FILE_URI}
	);
};

/** * Select picture from library */
function selectPicture() 
{    
	navigator.camera.getPicture(       
	 ...        
	 { 
	 	quality: 50, destinationType: navigator.camera.DestinationType.FILE_URI,         
	 	sourceType: navigator.camera.PictureSourceType.PHOTOLIBRARY}
	 );
};

var ft = new FileTransfer();
ft.upload(uri, serverUrl, successCallback, errorCallback, options);

//fileKey:  The name of the form element. 
//(default="file")fileName: The file name you want the file to be saved as on the server. 
//(default="image.jpg")mimeType: The mime type of the data you are uploading. 
//(default="image/jpeg")params:   A set of optional key/value pairs to be passed along in the HTTP request.

/** * Upload current picture */
function uploadPicture() 
{		
	// Get URI of picture to upload    
	var img = document.getElementById('camera_image');    
	var imageURI = img.src;    
	if (!imageURI || (img.style.display == "none")) 
	{        
		document.getElementById('camera_status').innerHTML = "Take picture or select picture from library first.";        
		return;    
	}        
	// Verify server has been entered    
	server = document.getElementById('serverUrl').value;    
	if (server) 
	{    	        
		// Specify transfer options        
		var options = new FileUploadOptions();        
		options.fileKey="file";        
		options.fileName=imageURI.substr(imageURI.lastIndexOf('/')+1);       
		options.mimeType="image/jpeg";        
		options.chunkedMode = false;        
		// Transfer picture to server        
		var ft = new FileTransfer();        
		ft.upload(imageURI, server, function(r) 
		{            
			document.getElementById('camera_status').innerHTML = "Upload successful: "+ r.bytesSent+" bytes uploaded.";            	        
		}, function(error) 
		{            
			document.getElementById('camera_status').innerHTML = "Upload failed: Code = "+ error.code;            	        
		}, 
		options);    
	}
}

