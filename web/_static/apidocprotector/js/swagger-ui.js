window.onload = function() {

    //Custom Api-Docs-Settings
    let url_api_docs_path = document.getElementById('hidden-api-docs-path').value;
    let swagger_layout = document.getElementById('swagger-layout').value;
    let show_url_api_docs = document.getElementById('show-url-api-docs').value;

    // Begin Swagger UI call region
    const ui = SwaggerUIBundle({
        url: url_api_docs_path,
        dom_id: '#swagger-ui',
        deepLinking: false,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: swagger_layout
    });
    // End Swagger UI call region

    window.ui = ui;

    (function() {
        setTimeout(function() {
            document.querySelectorAll('span.url')[0].style.color = '#4990e2';
            document.querySelectorAll('input.download-url-input')[0].style.color = '#131313';

            if (show_url_api_docs == "false") {
                document.getElementsByClassName('url')[0].innerHTML = '';
                document.querySelectorAll('input.download-url-input')[0].value = '';
            }
        }, 5000);
    })();

};