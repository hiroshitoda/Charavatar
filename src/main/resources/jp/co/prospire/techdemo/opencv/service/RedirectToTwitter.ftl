<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToTwitterView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>redirecting to Twitter... | Charavater</title>

<script src="/js/model/LanguageModel.js"></script>

<script>

var authUrl = '${authUrl}',
    words = LanguageModel.defaultWords;

if (authUrl.length <= 0)
{
    alert(words['no auth URL']);
    location.href = '/';
}

location.href = '${authUrl}';

</script>

</head>
</html>