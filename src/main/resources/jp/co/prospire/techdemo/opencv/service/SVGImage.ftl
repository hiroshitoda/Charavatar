<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.SVGCanvasView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0">

<title>Charavater</title>

<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/css/styles.css" rel="stylesheet">

<script src="/js/jquery-2.1.1.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/knockout-3.1.0.js"></script>

</head>
<body>

<div class="container">

    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-12 header">
            <a href="/"><img src="/image/header.png" alt="CHARAVATAR" /></a>
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-12">
            <div class="canvas">
                <img src="data:image/png;base64,${base64EncodedImage?html}" />
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-12 share">
            <span data-bind="text: shareUrlDescription"></span>
            <br />
            <input class="share-url-field col-xs-10 col-sm-8 col-md-6" type="text" data-bind="value: shareUrl" />
        </div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-12 share">
        
            <!-- twitter -->
            <div class="col-xs-6 col-sm-3 col-md-3 social-button">
                <a href="https://twitter.com/share" class="twitter-share-button" data-size="large" data-bind="attr: twitterButtonAttributes">Tweet</a>
                <script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0],p=/^http:/.test(d.location)?'http':'https';if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src=p+'://platform.twitter.com/widgets.js';fjs.parentNode.insertBefore(js,fjs);}}(document, 'script', 'twitter-wjs');</script>
            </div>

            <!-- facebook -->
            <div class="col-xs-6 col-sm-3 col-md-3 social-button">
                <iframe data-bind="attr: facebookIframeAttributes" scrolling="no" frameborder="0" style="border:none; overflow:hidden; height:21px; width:150px;" allowTransparency="true"></iframe>
            </div>
            
            <!-- hatena -->
            <div class="col-xs-6 col-sm-3 col-md-3 social-button">
                <a data-bind="attr: hatenaButtonAttributes" class="hatena-bookmark-button" data-hatena-bookmark-layout="standard-balloon" title="このエントリーをはてなブックマークに追加"><img src="http://b.st-hatena.com/images/entry-button/button-only@2x.png" alt="このエントリーをはてなブックマークに追加" width="20" height="20" style="border: none;" /></a>
                <script type="text/javascript" src="http://b.st-hatena.com/js/bookmark_button.js" charset="utf-8" async="async"></script>
            </div>
            
            <!-- google+ -->
            <div class="col-xs-6 col-sm-3 col-md-3 social-button">
                <div class="g-plusone" data-bind="attr: googleButtonAttributes"></div>
                <script type="text/javascript">
                    window.___gcfg = {lang: 'ja'};
                
                    (function() {
                        var po = document.createElement('script'); po.type = 'text/javascript'; po.async = true;
                        po.src = 'https://apis.google.com/js/platform.js';
                        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(po, s);
                    })();
                </script>
            </div>
  
        </div>
    </div>

</div>

<script>
var shortUrl = '${shortUrl?json_string}';
</script>

<script src="/js/model/LanguageModel.js"></script>
<script src="/js/view_model/SVGImageViewModel.js"></script>

</body>
</html>