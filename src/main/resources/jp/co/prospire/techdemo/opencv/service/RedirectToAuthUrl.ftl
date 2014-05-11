<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.RedirectToAuthUrlView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<title>redirecting... | Charavater</title>

<script>

try
{
    var storage = sessionStorage,
        contoursListJson = '${json?json_string}',
        contoursList = JSON.parse(contoursListJson);
    
    if (contoursList.length <= 0)
    {
        alert('no contour.');
        location.href = '/';
    }
    
    storage.setItem('ContoursList', contoursListJson);
}
catch (e)
{
    alert(e);
}

location.href = '/service/tweets/authUrl';

</script>

</head>
</html>