var LanguageModel = {};

(function(){
    
    var words,
        languageCode;
    
    LanguageModel.code =
    {
        ENGLISH: 1,
        JAPANESE: 81
    };
    languageCode = LanguageModel.code;
    
    LanguageModel.detectLanguage = function ()
    {
        var browserLanguage = navigator.browserLanguage ||
                                navigator.language ||
                                navigator.userLanguage,
            returnValue = 1;
        switch (browserLanguage)
        {
            case 'ja':
            case 'ja-jp':
                returnValue = languageCode.JAPANESE;
                break;
            default:
                returnValue = languageCode.ENGLISH;
        }
        return returnValue;
    };
    
    LanguageModel.words = {};
    
    LanguageModel.words[languageCode.ENGLISH] =
    {
        'teaser src': 'teaser.en',
        'first step': '1. take your selfie.',
        'second step': '2. push selfie to CHARAVATAR.',
        'twitter data-lang': 'en-US',
        'twitter data-text': 'I made my avatar with CHARAVATAR. Please look at it! ::',
        'share URL description': 'You can share this avatar with below URL.',
        'hatena data-hatena-bookmark-lang': 'en'
    };
    
    LanguageModel.words[languageCode.JAPANESE] =
    {
        'teaser src': 'teaser.ja',
        'first step': '1. 自画撮りしよう',
        'second step': '2. CHARAVATARに送信しよう',
        'twitter data-lang': 'ja',
        'twitter data-text': 'CHARAVATAR でアバターを作りました。 みんな見てね！ ::',
        'share URL description': 'このアバターは次のURLで共有できます。',
        'hatena data-hatena-bookmark-lang': 'ja'
    };
    
    LanguageModel.defaultWords = LanguageModel.words[LanguageModel.detectLanguage()];
}());