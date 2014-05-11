<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToTwitterView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>redirecting... | Charavater</title>

<script>

var authUrl = '${authUrl}';

if (authUrl.length <= 0)
{
    alert('no auth URL.');
    location.href = '/';
}

location.href = '${authUrl}';

</script>

</head>
</html>