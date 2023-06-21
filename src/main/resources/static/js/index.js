document.addEventListener('DOMContentLoaded', function() {
    var viewJsonButton = document.getElementById('json');
    var searchInput = document.querySelector('.input');

    viewJsonButton.addEventListener('click', function() {
        var searchText = searchInput.value;

        var xhr = new XMLHttpRequest();
        xhr.open('GET', '/consulta?query=' + encodeURIComponent(searchText), true);
        xhr.setRequestHeader('Accept', 'application/json');
        xhr.onreadystatechange = function() {
            if(xhr.readyState === XMLHttpRequest.DONE) {
                if(xhr.status === 200) {
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
                    console.error('Error en la solicitud:', xhr.status);
                }
            }
        };
        xhr.send();
    });
});