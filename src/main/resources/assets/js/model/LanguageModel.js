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
        'no contours': 'No contours found. Please take your selfie again.',
        'contours': 'Contours found as below.',
        'redirecting to Twitter': 'redirecting to Twitter...',
        'no auth URL': 'CHARAVATAR failed to get Twitter authentication URL.',
        'no tweets': 'No tweets found. Back to top...',
        'completed': 'Completed to trace your selfie with your tweets!',
        'share this avatar': 'share this avatar',
        'return': 'return to the first',
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
        'no contours': '写真の輪郭を見つけられませんでした。写真を撮り直してください。',
        'contours': '写真の輪郭はこんな感じになりました。',
        'redirecting to Twitter': 'Twitter に接続しています...',
        'no auth URL': 'Twitterの認証用URLを取得できませんでした。',
        'no tweets': 'ツイートを取得できませんでした。最初からやり直してください。',
        'completed': '写真の輪郭をツイートでなぞりました！',
        'share this avatar': 'このアバターを公開する',
        'return': '最初からやり直す',
        'twitter data-lang': 'ja',
        'twitter data-text': 'CHARAVATAR でアバターを作りました。 みんな見てね！ ::',
        'share URL description': 'このアバターは次のURLで共有できます。',
        'hatena data-hatena-bookmark-lang': 'ja'
    };
    
    LanguageModel.defaultWords = LanguageModel.words[LanguageModel.detectLanguage()];
}());