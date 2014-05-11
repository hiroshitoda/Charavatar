var IndexViewModel;

(function(){
    
    var words = LanguageModel.defaultWords;
    
    IndexViewModel = function IndexViewModel()
    {
        var self = this;
        
        self.uploadFilePath = ko.observable();
        
        self.isSubmitEnableImpl = function isSubmitEnableImpl()
        {
            var _uploadFilePath = self.uploadFilePath();
            if (_uploadFilePath !== undefined && _uploadFilePath.length > 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        };
        self.isSubmitEnable = ko.computed(self.isSubmitEnableImpl);
        
        self.firstStepText = words['first step'];
        self.secondStepText = words['second step'];
        
        self.firstStepStyleImpl = function isSubmitEnableImpl()
        {
            var _uploadFilePath = self.uploadFilePath();
            if (_uploadFilePath !== undefined && _uploadFilePath.length > 0)
            {
                return {
                    'background-color': '#b0b0b0',
                    'font-size': '100%'
                };
            }
            else
            {
                return {
                    'background-color': '#baf9ff',
                    'font-size': '150%'
                };
            }
        };
        self.firstStepStyle = ko.computed(self.firstStepStyleImpl);
        
        self.teaserTextAttributes =
        {
            src: '/image/' + words['teaser src'] + '.png'
        };
        
        self.teaserImage1Attributes =
        {
            src: '/image/teaserImage1.jpg'
        };

        self.teaserImage2Attributes =
        {
            src: '/image/teaserImage2.jpg'
        };
    };

    ko.applyBindings(new IndexViewModel);
}());