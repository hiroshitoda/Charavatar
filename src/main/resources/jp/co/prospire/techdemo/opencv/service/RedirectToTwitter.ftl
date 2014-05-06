<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToTwitterView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<title>redirecting... | Charavater</title>

<script>

var authUrl = '${authUrl}';

if (authUrl.length <= 0)
{
    throw new Error('no auth URL.');
}

location.href = '${authUrl}';

</script>

</head>
</html>