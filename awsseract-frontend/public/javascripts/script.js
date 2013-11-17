function TaskResultController('SearchCtrl', function ($scope) {
        $scope.tasks = [];

        // handles the callback from the received event
                var handleCallback = function (msg) {
                    $scope.$apply(function () {
                        $scope.results = JSON.parse(msg.data)
                    });
                }

                var source = new EventSource('/resultfeed');
                source.addEventListener('results', handleCallback, false);

        }
    });
