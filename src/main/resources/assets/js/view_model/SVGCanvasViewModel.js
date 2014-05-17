var SVGCanvasViewModel;

(function(){

    var words = LanguageModel.defaultWords;
    
    SVGCanvasViewModel = function SVGCanvasViewModel(tweetWordsJson)
    {
        var storage = sessionStorage,
            tweetWords = JSON.parse(tweetWordsJson),
            serializedWords = '',
            contoursListJson = storage.getItem('ContoursList'),
            contoursList = JSON.parse(contoursListJson),
            contoursListLength = 0,
            svg = '',
            path = '',
            textPath = '';
        
        self.isSubmitEnable = ko.observable(false);
    
        if (contoursList === null || contoursList.length <= 0)
        {
            alert(words['no contours']);
            location.href = '/';
        }

        contoursListLength = contoursList.length;
        storage.clear();

        if (tweetWords === null || contoursList.length <= 0)
        {
            alert('no tweet.');
            location.href = '/';
        }
    
        self.getSerializedWords = function getSerializedWords(tweetWords)
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
        };
        serializedWords = self.getSerializedWords(tweetWords);

        self.contoursListToPathElements = function contoursListToPathElements(contoursList)
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
                                '" fill="none" stroke="#000000" strokeWidth="2" d="';
    
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
        };
        path = self.contoursListToPathElements(contoursList);

        self.tweetWordsToTextPathElements = function tweetWordsToTextPathElements(contoursList, serializedWords)
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
    
                    textPath += '<text><textPath xlink:href="#textpath-' +
                                    contoursListIndex +
                                    '-' +
                                    contoursIndex +
                                    '">' +
                                    serializedWords.substr(
                                            Math.floor(Math.random() * (serializedWords.length - characterLength)),
                                            characterLength
                                        )
                                        .replace(/\&([^a]*)/g, '&amp;$1') +
                                    '</textPath></text>';
                }
            }
            return textPath;
        };
        textPath = self.tweetWordsToTextPathElements(contoursList, serializedWords);
    
        svg = '<svg xmlns="http://www.w3.org/2000/svg"' +
                    ' xmlns:xlink="http://www.w3.org/1999/xlink"' +
                    ' version="1.1"' +
                    ' id="charavatar" width="100%" height="100%" viewBox="0 0 480 480"><defs>' +
                    path +
                    '</defs><g font-size="12px">' +
                    textPath +
                    '</g></svg>';

        self.svgValue = ko.observable(svg);

        self.progressText = words['completed'];
        self.isSubmitEnable(true);
        self.shareAvatarText = words['share this avatar'];
        self.returnText = words['return'];
    };
    
    ko.applyBindings(new SVGCanvasViewModel(this.tweetJsonString));
    
}());