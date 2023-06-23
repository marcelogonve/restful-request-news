// TODO se puede mejorar el proceso de las solicitudes. Se repite mucho código
document.addEventListener('DOMContentLoaded', function() {
    var downloadJsonButton = document.getElementById('json');
    var downloadXmlButton = document.getElementById('xml');
    var downloadHtmlButton = document.getElementById('html');
    var downloadTextPlainButton = document.getElementById('text-plain');
    var searchInput = document.querySelector('.input');

    downloadJsonButton.addEventListener('click', function() {
        var searchText = searchInput.value;
        var includeImage = confirm('¿Deseas incluir la foto codificada en base64 y su content-type?');

        var xhr = new XMLHttpRequest();
        var url = '/consulta?q=' + encodeURIComponent(searchText);
        if(includeImage) {
            url += '&f=true';
        }
        document.getElementById('spinner').style.display = 'block';
        document.getElementById('spinner').style.position = 'absolute';
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Accept', 'application/json');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === XMLHttpRequest.DONE) {
                if(xhr.status === 200) {
                    document.getElementById('spinner').style.display = 'none';
                    var response = xhr.response;
                    var jsonString = JSON.stringify(response);
                    var cleanedJsonString = jsonString.replace(/\\/g, '')
                                            .replace(/^\"/, '')
                                            .replace(/\"$/, '');

                    var blob = new Blob([cleanedJsonString], { type: 'application/json' });

                    var downloadLink = document.createElement('a');
                    downloadLink.href = URL.createObjectURL(blob);
                    downloadLink.download = 'resultado.json';
                    downloadLink.style.display = 'none';

                    document.body.appendChild(downloadLink);

                    downloadLink.click();

                    document.body.removeChild(downloadLink);
                } else {
                    document.getElementById('spinner').style.display = 'none';
                    var errorResponse = JSON.parse(xhr.responseText);
                    alert(errorResponse.error + '. Código: ' + errorResponse.code);
                }
            }
        };
        xhr.send();
    });

    downloadXmlButton.addEventListener('click', function() {
        var searchText = searchInput.value;

        var xhr = new XMLHttpRequest();
        var url = '/consulta?q=' + encodeURIComponent(searchText) + '&f=false';
        document.getElementById('spinner').style.display = 'block';
        document.getElementById('spinner').style.position = 'absolute';
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Accept', 'application/xml');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === XMLHttpRequest.DONE) {
                if(xhr.status === 200) {
                    document.getElementById('spinner').style.display = 'none';
                    var response = xhr.response;
                
                    var blob = new Blob([response], { type: 'application/xml' });

                    var downloadLink = document.createElement('a');
                    downloadLink.href = URL.createObjectURL(blob);
                    downloadLink.download = 'resultado.xml';
                    downloadLink.style.display = 'none';

                    document.body.appendChild(downloadLink);

                    downloadLink.click();

                    document.body.removeChild(downloadLink);
                } else {
                    document.getElementById('spinner').style.display = 'none';
                    alert(xhr.responseText);
                }
            }
        };
        xhr.send();
    });

    downloadHtmlButton.addEventListener('click', function() {
        var searchText = searchInput.value;

        var xhr = new XMLHttpRequest();
        var url = '/consulta?q=' + encodeURIComponent(searchText) + '&f=false';
        document.getElementById('spinner').style.display = 'block';
        document.getElementById('spinner').style.position = 'absolute';
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Accept', 'text/html');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === XMLHttpRequest.DONE) {
                if(xhr.status === 200) {
                    document.getElementById('spinner').style.display = 'none';
                    var response = xhr.response;

                    var blob = new Blob([response], { type: 'text/html' });

                    var downloadLink = document.createElement('a');
                    downloadLink.href = URL.createObjectURL(blob);
                    downloadLink.download = 'resultado.html';
                    downloadLink.style.display = 'none';

                    document.body.appendChild(downloadLink);

                    downloadLink.click();

                    document.body.removeChild(downloadLink);
                } else {
                    document.getElementById('spinner').style.display = 'none';
                    alert(xhr.responseText);
                }
            }
        };
        xhr.send();
    });

    downloadTextPlainButton.addEventListener('click', function() {
        var searchText = searchInput.value;

        var xhr = new XMLHttpRequest();
        var url = '/consulta?q=' + encodeURIComponent(searchText) + '&f=false';
        document.getElementById('spinner').style.display = 'block';
        document.getElementById('spinner').style.position = 'absolute';
        xhr.open('GET', url, true);
        xhr.setRequestHeader('Accept', 'text/plain');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === XMLHttpRequest.DONE) {
                if(xhr.status === 200) {
                    document.getElementById('spinner').style.display = 'none';
                    var response = xhr.response;

                    var blob = new Blob([response], { type: 'text/plain' });

                    var downloadLink = document.createElement('a');
                    downloadLink.href = URL.createObjectURL(blob);
                    downloadLink.download = 'resultado.txt';
                    downloadLink.style.display = 'none';

                    document.body.appendChild(downloadLink);

                    downloadLink.click();

                    document.body.removeChild(downloadLink);
                } else {
                    document.getElementById('spinner').style.display = 'none';
                    alert(xhr.responseText);
                }
            }
        };
        xhr.send();
    });
});