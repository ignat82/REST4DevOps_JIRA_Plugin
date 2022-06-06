const settingsForm = document.getElementById("setSettings");

// alert(settingsForm.getAttribute('method'));
function submitSettings() {
    // alert('settings submitted');
    const request = new XMLHttpRequest();
    const formData = new FormData(settingsForm);
    var result = {};
    for (var entry of formData.entries())
    {
        result[entry[0]] = entry[1];
    }
    console.log(result);
    result = JSON.stringify(result)
    console.log(result);
    const URI = document.getElementById('settingsURI').innerText;
    // alert(URI);
    // alert(FD.get('settings_string'));
    request.open( "POST", URI, true);
    // request.setRequestHeader("Content-Type", "application/json");
    // request.setRequestHeader("Content-length", 1);
    // request.setRequestHeader("Connection", "close");
    request.addEventListener( 'error', function(event) {
        // alert( 'Oops! Something went wrong.' );
    } );
    request.addEventListener( 'load', function(event) {
        alert( 'All goes well' );
    } );
    request.onreadystatechange = function () {
        if (request.readyState === 4 && request.status === 200) {
            var json = JSON.parse(xhr.responseText);
            console.log(json.email + ", " + json.password);
        } else {
            console.log(request.status);
        }
    };
    console.log("sending");
    request.send(result);


}
