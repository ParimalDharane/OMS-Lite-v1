(function () {
    'use strict';

    angular.module('OMSLiteApp').factory('omsliteService', omsliteService);

    omsliteService.$inject = ['$http', '$q'];

    function omsliteService($http, $q) {
        return {
            save: save
            ,close: close
            ,isOrderEntryAllowed: isOrderEntryAllowed
            ,isExecutionEntryAllowed: isExecutionEntryAllowed
            ,isOrderbookClosable: isOrderbookClosable
            ,saveOrder: saveOrder
            ,saveExecution: saveExecution
            ,findAllStats: findAllStats
        };
        function save(orderbook) {
            var deferred = $q.defer();
            $http.post('api/orderbooks', orderbook).then(function (data) {
                deferred.resolve(data);
            }, function (err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }
        function close(orderbook) {
            var deferred = $q.defer();
            orderbook.status = 'Close';
            $http.put('api/orderbooks/'+orderbook.id).then(function (data) {
                deferred.resolve(data);
            }, function (err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }
        function isOrderEntryAllowed(orderbook) {
            if(orderbook && orderbook.status && orderbook.status === 'Open') {
                return true;
            }
            return false;
        }
        function isExecutionEntryAllowed(orderbook) {
            if(orderbook && orderbook.status && orderbook.status === 'Close') {
                return true;
            }
            return false;
        }
        function isOrderbookClosable(orderbook) {
            if(orderbook && orderbook.status && orderbook.status === 'Open') {
                return true;
            }
            return false;
        }
        function saveOrder(bookId, order) {
            var deferred = $q.defer();
            $http.post('api/orderbooks/' + bookId + '/orders', order).then(function (data) {
                deferred.resolve(data);
            }, function (err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }
        function saveExecution(bookId, execution) {
            var deferred = $q.defer();
            $http.post('api/orderbooks/' + bookId + '/executions', execution).then(function (data) {
                deferred.resolve(data);
            }, function (err) {
                deferred.reject(err);
            });
            return deferred.promise;
        }
        function findAllStats(id) {
            var deferred = $q.defer();
            $http.get('api/stats/' + id).then(function (data) {
                deferred.resolve(data);
            }, function (err) {
                deferred.reject(err);
            });

            return deferred.promise;
        }        
    }
})();