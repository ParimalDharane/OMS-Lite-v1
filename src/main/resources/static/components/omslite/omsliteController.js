(function () {
	'use strict';

	angular.module('OMSLiteApp').controller('omsliteController', [
		'$scope', 'toaster', 'omsliteService', omsliteController
	]);

	function omsliteController($scope, toaster, omsliteService) {

		//Common 
		$scope.dateFormat = 'yyyy-MM-dd HH:mm:ss';
		// $scope.dateFormat = 'yyyy-MM-dd HH:mm:ss Z';
		$scope.orderFilter = '';
		$scope.executionFilter = '';
		$scope.loadingCount = 0;

		//Orderbook entity
		$scope.selectedBook = {};
		$scope.orderbookCloseFlag = false;
		$scope.orderbookSelectedFlag = false;
		// $scope.orderbookChanged = {};
		$scope.orderbookList = [];
		$scope.orderbookObj = {
			instrumentName: 'Inst1'
		};

		//OrderbookStats
		$scope.orderbookStats = {};
		$scope.orderbookStatsValid = {};
		$scope.orderbookStatsInvalid = {};

		//Order entity
		$scope.orderEntryFlag = false;
		$scope.orderObj = {
			instrumentName: '',
			price: 10.0,
			marketOrder: false,
			quantity: 100.0
		};

		//Execution entity
		$scope.executionEntryFlag = false;
		$scope.executionObj = {
			instrumentName: '',
			price: 10.0,
			quantity: 100.0
		};

		var showSuccess = function(message) {
			toaster.pop('success', "", message);
		}
		var showInfo = function(message) {
			toaster.pop('', "", message);
		}
		var showError = function(message) {
			toaster.pop('error', "", message);
		}
		$scope.showAddOrder = function () {
			// $scope.orderEntryMode = true;
		}
		$scope.saveOrderbook = function (orderbook) {
			// console.log(orderbook, ' saving orderbook ...');
			setLoadingOn();
			omsliteService.save(orderbook).then(function (result) {
				angular.element('#orderbookEntryModal').modal('hide');
				setLoadingOff();
				showSuccess("Orderbook for '" + orderbook.instrumentName + "' saved successfully.");
				$scope.orderbookList.push(result.data);
				$scope.selectedBook = result.data;
				onSuccess();
			}, function (error) {
				setLoadingOff();
				showError('Failed - ' + error.data.message);
			});
		};
		$scope.orderbookChanged = function (currBook) {
			$scope.selectedBook = currBook;
			if ($scope.selectedBook) {
				onSuccess();

				$scope.orderObj.instrumentName = currBook.instrumentName;
				$scope.executionObj.instrumentName = currBook.instrumentName;
				$scope.orderbookSelectedFlag = true;
				
			} else {
				$scope.orderbookSelectedFlag = false;
			}
		};
		$scope.closeOrderbook = function (currBook) {
			$scope.selectedBook = currBook;
			setLoadingOn();
			omsliteService.close($scope.selectedBook).then(function (result) {
				setLoadingOff();
				showSuccess("Orderbook for '" + $scope.selectedBook.instrumentName + "' closed successfully.");
				onSuccess();
			}, function (error) {
				setLoadingOff();
				showError('Error while saving orderbook - ' + error.data.message);
			});

		};
		//Orderbook Stats
		$scope.loadOrderbookStats = function (orderbook) {
			if ($scope.selectedBook && $scope.selectedBook.id) {
				setLoadingOn();
				omsliteService.findAllStats($scope.selectedBook.id).then(function (result) {
					$scope.orderbookStats = result.data;
					setLoadingOff();
					// console.log($scope.orderbookStats, ' Data received from orderbookStatsService');
				}, function (error) {
					setLoadingOff();
					showError('Error while loading orderbookStats - ' + error);
					$scope.orderbookStats = {};
				});
			} else {
				$scope.orderbookStats = {};
			}
		};
		$scope.loadOrderbookStatsValid = function () {
			if ($scope.selectedBook && $scope.selectedBook.instrumentName) {
				setLoadingOn();
				orderbookStatsService.findAllValid($scope.selectedBook.instrumentName).then(function (result) {
					
					$scope.orderbookStatsValid = result.data;
					setLoadingOff();
					// console.log($scope.orderbookStatsValid, ' orderbookStatsValid received from orderbookStatsService');
				}, function (error) {
					setLoadingOff();
					console.log('Error while loading orderbookStatsValid - ' + error);
					$scope.orderbookStatsValid = {};
				});
			} else {
				$scope.orderbookStatsValid = {};
			}
		};
		$scope.loadOrderbookStatsInvalid = function () {
			if ($scope.selectedBook && $scope.selectedBook.instrumentName) {
				setLoadingOn();
				orderbookStatsService.findAllInvalid($scope.selectedBook.instrumentName).then(function (result) {
					
					$scope.orderbookStatsInvalid = result.data;
					setLoadingOff();
					// console.log($scope.orderbookStatsInvalid, ' orderbookStatsInvalid received from orderbookStatsService');
				}, function (error) {
					setLoadingOff();
					console.log('Error while loading orderbookStatsInvalid - ' + error);
					$scope.orderbookStatsInvalid = {};
				});
			} else {
				$scope.orderbookStatsInvalid = {};
			}
		};
		$scope.showOrderbookStatsReport = function() {
			$scope.loadOrderbookStatsValid();
			$scope.loadOrderbookStatsInvalid();
			angular.element('#ostatsReportModal').modal('show');
		}
		$scope.saveOrder = function (order) {
			setLoadingOn();
			enrichOutboundOrder(order)
			omsliteService.saveOrder($scope.selectedBook.id, order).then(function (result) {
				angular.element('#orderEntryModal').modal('hide');
				setLoadingOff();
				showSuccess("Order for '" + order.instrumentName + "' saved successfully.");
				onSuccess();
			}, function (error) {
				setLoadingOff();
				showError('Error while saving order data - ' + error.data.message);
			});
		};
		$scope.saveExecution = function (execution) {
			enrichOutboundExecution(execution)
			setLoadingOn();
			omsliteService.saveExecution($scope.selectedBook.id, execution).then(function (result) {
				angular.element('#executionEntryModel').modal('hide');
				setLoadingOff();
				showSuccess("Execution for '" + execution.instrumentName + "' saved successfully.");
				onSuccess();
			}, function (error) {
				setLoadingOff();
				showError('Error while saving execution data - ' + error.data.message);
			});
		};
		var enrichOutboundOrder = function (order) {
			order.instrumentName = $scope.selectedBook.instrumentName;
		}
		var enrichOutboundExecution = function (execution) {
			execution.instrumentName = $scope.selectedBook.instrumentName;
		}
		var onSuccess = function() {
			updateEntryActions();
			$scope.loadOrderbookStats();
		}
		var updateEntryActions = function () {
			//disable Add Order if status not open
			$scope.orderEntryFlag = omsliteService.isOrderEntryAllowed($scope.selectedBook);
			//disable Add Execution if status not close
			$scope.executionEntryFlag = omsliteService.isExecutionEntryAllowed($scope.selectedBook);
			//disable Close Orderbook if status not open
			$scope.orderbookCloseFlag = omsliteService.isOrderbookClosable($scope.selectedBook);
		}
		var setLoadingOn = function() {
			$scope.loadingCount++;
		}
		var setLoadingOff = function() {
			$scope.loadingCount--;
		}
		var init = function () {
		};

		init();
	}
})();