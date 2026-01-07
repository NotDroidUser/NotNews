function processArticle(html,articleImageUrl) {
    try {
        var parser = new DOMParser()
        Android.setProgress(20)
        var doc = parser.parseFromString(html, 'text/html')
        Android.setProgress(60)
        var article = new Readability(doc).parse()
        Android.setProgress(80)
        if (article) {
            var cleanContent = DOMPurify.sanitize(article.content)
            Android.setProgress(90)
            document.body.innerHTML = '<div class="reader-container">' +
                '<h1 class="reader-title">' + article.title + '</h1>' +
                '<img src=\"'+ articleImageUrl +'\">'+'</img>'+
                cleanContent +
                '</div>'
            applyStyles()
            Android.setProgress(100)
            Android.receiveProcessedArticle(article.title, document.body.innerHTML)
        } else {
            Android.receiveProcessedArticle('999', '<p>Failed to extract article content.</p>')
        }
    } catch (e) {
        console.error('Error:', e)
        document.body.innerHTML = '<p>Error processing article.</p>'
        Android.receiveProcessedArticle('', '<p>Error processing article.</p>')
    }
}

function putArticle(html) {

    try {
        Android.setProgress(60)
        document.body.innerHTML = html
        Android.setProgress(80)
        applyStyles()
        Android.setProgress(100)
    } catch (e) {
        console.error('Error:', e)
        document.body.innerHTML = '<p>Error processing article.</p>'
        Android.receiveProcessedArticle('', '<p>Error processing article.</p>')
    }
}


function applyStyles() {
    var style = document.createElement('style')
    style.innerHTML = Android.getStyle()
    document.head.appendChild(style)
}
