var SVGImageViewModel;

(function(){
    
    var words = LanguageModel.defaultWords;
    
    SVGImageViewModel = function SVGImageViewModel(shortUrl)
    {
        var self = this;
        
        self.shareUrl = 'http://charavatar.me/service/share/' + shortUrl;
        self.shareUrlDescription = words['share URL description'];

        self.twitterButtonAttributes =
        {
            'data-lang': words['twitter data-lang'],
            'data-text': words['twitter data-text'],
            'data-url': self.shareUrl
        };
        
        self.facebookIframeAttributes =
        {
            'src': '//www.facebook.com/plugins/like.php?href=' +
                        encodeURIComponent(self.shareUrl) +
                        '&width=140&layout=button_count&action=like&show_faces=false&share=true&height=21'
        };
        
        self.hatenaButtonAttributes =
        {
            'href': 'http://b.hatena.ne.jp/entry/' + self.shareUrl,
            'data-hatena-bookmark-lang': words['hatena data-hatena-bookmark-lang']
        };
        
        self.googleButtonAttributes =
        {
            'data-href': self.shareUrl
        };
    };
    
    ko.applyBindings(new SVGImageViewModel(shortUrl));
    
}());