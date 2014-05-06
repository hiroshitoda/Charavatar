<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToAuthUrlView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<title>redirecting... | Charavater</title>

<script>

var storage = sessionStorage,
    json = '${json?json_string}';

if (json.length <= 0)
{
    throw new Error('no contours.');
}

storage.setItem('ContoursList', json);

location.href = '/tweets/authUrl';

</script>

</head>
</html>