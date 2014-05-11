<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.SVGCanvasView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0">

<title>processing SVG... | Charavater</title>

<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/css/styles.css" rel="stylesheet">

<script src="/js/jquery-2.1.1.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/knockout-3.1.0.js"></script>

</head>
<body>

<form id="svg_form" action="/service/share" method="POST">

<input id="svg" name="svg" type="hidden" data-bind="value: svgValue" /> 

</form>

<script>
var tweetJsonString = '${json?json_string}';
</script>
<script src="/js/view_model/SVGCanvasViewModel.js"></script>

</body>
</html>