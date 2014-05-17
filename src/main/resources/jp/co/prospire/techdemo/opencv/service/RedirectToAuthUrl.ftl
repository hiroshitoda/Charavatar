<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToAuthUrlView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0">

<title>redirecting... | Charavater</title>


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
        <div class="col-xs-12 col-sm-12 col-md-12 charavatar-progress" data-bind="html: progressText"></div>
    </div>

    <div class="row">
        <div class="col-xs-12 col-sm-12 col-md-12">
            <div class="canvas" data-bind="html: svgValue"></div>
        </div>
    </div>

</div>

<script>
var contoursListJson = '${json?json_string}';
</script>

<script src="/js/model/LanguageModel.js"></script>
<script src="/js/view_model/RedirectToAuthUrlViewModel.js"></script>

</head>
</html>