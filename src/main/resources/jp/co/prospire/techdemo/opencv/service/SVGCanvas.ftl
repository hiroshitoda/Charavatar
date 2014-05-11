<#-- @ftlvariable name="" type="jp.co.prospire.techdemo.opencv.service.SVGCanvasView" -->
<!doctype html>
<html lang="ja">
<head>

<meta charset="UTF-8" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width,initial-scale=1.0">

<title>Result | Charavater</title>

<link href="/css/bootstrap.min.css" rel="stylesheet">
<link href="/css/bootstrap-theme.min.css" rel="stylesheet">
<link href="/css/styles.css" rel="stylesheet">

<script src="/js/jquery-2.1.1.min.js"></script>
<script src="/js/bootstrap.min.js"></script>

</head>
<body>

<div class="container">

    <div class="row">
        <div class="col-xs-12 col-sm-8 col-md-7">
            <div id="canvas">
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-xs-12 col-sm-8 col-md-7">
            <a href="/">back to start.</a>
        </div>
    </div>

</div>

<script>

try
{
    var storage = sessionStorage,
        tweetWordsJson = '${json?json_string}',
        tweetWords = JSON.parse(tweetWordsJson),
        serializedWords = '',
        contoursListJson = storage.getItem('ContoursList'),
        contoursList = JSON.parse(contoursListJson),
        contoursListLength = 0,
        svg = '',
        path = '',
        textPath = '';
    
    if (contoursList === null || contoursList.length <= 0)
    {
        alert('no contour.');
        location.href = '/';
    }
    
    contoursListLength = contoursList.length;
    storage.clear();
    
    if (tweetWords === null || contoursList.length <= 0)
    {
        alert('no tweet.');
        location.href = '/';
    }
    
    serializedWords = getSerializedWords(tweetWords);
    path = contoursListToPathElements(contoursList);
    textPath = tweetWordsToTextPathElements(contoursList, serializedWords);
    
    svg = '<svg id="charavatar" width="480" height="480"><token="hogehogehoge" /><defs>' +
                path +
                '</defs><g font-size="12px"><text>' +
                textPath +
                '</text></g></svg>';
    document.getElementById('canvas').innerHTML = svg;
}
catch (e)
{
    alert(e);
}

function getSerializedWords(tweetWords)
{
    var serializedWords = '',
        word = '';

    for (word in tweetWords)
    {
        if (tweetWords.hasOwnProperty(word))
        {
            serializedWords += word;
        }
    }
    return serializedWords.replace(/[0-9]+/g, '');
}

function contoursListToPathElements(contoursList)
{
    var path = '',
        contoursListIndex = 0,
        contoursIndex = 0,
        pointsIndex = 0,
        contours = [],
        thresholdLevel = 0,
        contoursLength = 0,
        points = [],
        pointsLength = 0,
        point = {};

    for (contoursListIndex = 0; contoursListIndex < contoursListLength; contoursListIndex++)
    {
        contours = contoursList[contoursListIndex].contours,
        thresholdLevel = contoursList[contoursListIndex].thresholdLevel,
        contoursLength = contours.length;
        
        for (contoursIndex = 0; contoursIndex < contoursLength; contoursIndex++)
        {
            points = contours[contoursIndex].points,
            pointsLength = points.length;
                
            path += '<path id="textpath-' +
                        contoursListIndex +
                        '-' +
                        contoursIndex +
                        '" fill="none" stroke="#0000' +
                        Math.floor(thresholdLevel * 0.39) +
                        '" strokeWidth="2" d="';

            for (pointsIndex = 0; pointsIndex < pointsLength; pointsIndex++)
            {
                point = points[pointsIndex];
                if (pointsIndex === 0)
                {
                    path += 'M';
                }
                else
                {
                    path += ' L';
                }
                path += point.x + ',' + point.y;
            }
            path += ' z" />';
        }
    }
    return path;
}

function tweetWordsToTextPathElements(contoursList, serializedWords)
{
    var textPath = '',
        contoursListIndex = 0,
        contours = [],
        contoursLength = 0,
        contoursIndex = 0,
        characterLength = 0;

    for (contoursListIndex = 0; contoursListIndex < contoursListLength; contoursListIndex++)
    {
        contours = contoursList[contoursListIndex].contours,
        contoursLength = contours.length;
        
        for (contoursIndex = 0; contoursIndex < contoursLength; contoursIndex++)
        {
            characterLength = Math.floor(contours[contoursIndex].arcLength);

            textPath += '<textPath xlink:href="#textpath-' +
                            contoursListIndex +
                            '-' +
                            contoursIndex +
                            '">' +
                            serializedWords.substr(
                                    Math.floor(Math.random() * (serializedWords.length - characterLength)),
                                    characterLength) +
                            '</textPath>';
        }
    }
    return textPath;
}

</script>

</body>
</html>