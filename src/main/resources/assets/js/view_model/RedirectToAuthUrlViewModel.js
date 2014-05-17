var RedirectToAuthUrlViewModel;

(function(){
    
    var words = LanguageModel.defaultWords;
    
    RedirectToAuthUrlViewModel = function RedirectToAuthUrlViewModel(contoursListJson)
    {
        var storage = sessionStorage,
            contoursList = JSON.parse(contoursListJson),
            contoursListLength = 0,
            svg = '',
            path = '';
        
        if (contoursList === null || contoursList.length <= 0)
        {
            alert(words['no contours']);
            location.href = '/';
        }

        contoursListLength = contoursList.length;
        storage.setItem('ContoursList', contoursListJson);
        
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
        
        svg = '<svg xmlns="http://www.w3.org/2000/svg"' +
        ' xmlns:xlink="http://www.w3.org/1999/xlink"' +
        ' version="1.1"' +
        ' id="charavatar" width="100%" height="100%" viewBox="0 0 480 480">' +
        path +
        '</svg>';

        self.svgValue = ko.observable(svg);
        
        self.progressText = words['contours'] + '<br />' + words['redirecting to Twitter'];
    };
    
    ko.applyBindings(new RedirectToAuthUrlViewModel(contoursListJson));

    setTimeout(
            function()
            {
                location.href = '/service/tweets/authUrl';
            },
            1000
        );

}());