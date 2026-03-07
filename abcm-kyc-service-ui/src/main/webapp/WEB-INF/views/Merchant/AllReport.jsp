<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<head>
<%@ include file="MerchantHeader.jsp"%>
<meta charset="UTF-8">
<title>eKYC | Transaction Report</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<div class="pc-container">
	<div class="dh-350"></div>
	<div class="pc-content neg-h">
		<!-- [ breadcrumb ] start -->
		<div class="page-header">
			<div class="page-block">
				<div class="row align-items-center">
					<div class="col-md-12">
						<div class="page-header-title">
							<h2 class="mb-0">Transaction Reports</h2>
						</div>
					</div>
				</div>
			</div>
		</div>
        <div class="card tt-k mt-2">
            <div class="card-body p-3">
                <form action="${url}/app/public/downloadTxnReport" method="post">
                    <input type="hidden" name="mid" value="${merchant.mid}">
                    <input type="hidden" name="UserType" value="user">
                    <div class="row dym-forms">
                        <!-- Date Range Picker -->
                        <div class="col-md-4 col-12">
                            <div class="form-group">
                            <label for="pc-date_range_picker-2" class="form-label">Select Date Range</label>
                            <div class="input-group">
                                <span class="input-group-text"> <i class="feather icon-calendar"></i> </span>
                                <input type="text" id="pc-date_range_picker-2" class="form-control" placeholder="Select date range" name="dateRange" />
                            </div>
                            </div>
                        </div>

                        <div class="col-md-4 col-12">
                            <div class="form-group">
                            <label for="productSelect" class="form-label">Select Product</label>
                            <select class="form-select" id="productSelect" name="product">
                                <c:if test="${not empty merchant && merchant.aadharOkyc=='ENABLE'}">
                                <option value="OKYC">OKYC</option>
                                </c:if>
                                <c:if test="${not empty merchant && merchant.panPro=='ENABLE'}">
                                <option value="PAN">PAN</option>
                                </c:if>
                                <c:if test="${not empty merchant && merchant.gstLit=='ENABLE'}">
                                <option value="GSTIN">GSTIN</option>
                                </c:if>
                                
                                  <c:if test="${not empty merchant && merchant.drivingLicense=='ENABLE'}">
                               <option value="DRIVING_LICENSE">DRIVING_LICENSE</option>
                                </c:if>
                                
                                  <c:if test="${not empty merchant && merchant.voterId=='ENABLE'}">
                                <option value="voter-id">VOTER-ID</option>
                                </c:if>
                                 <option value="e-sign">E-SIGN</option>
                                <option value="allproduct">All Product</option>
                            </select>
                            </div>
                        </div>
                        <div class="col-md-4 col-12 mt-4">
                            <div class="d-grid gap-2 d-md-flex mt-1">
                                <button type="button"  id="kycReport_Merchnat"class="btn btn-shadow btn-info btn-sm w-100 rounded-0" formaction="${url}/app/public/submitTxnReport">
                                    <i class="fa fa-save me-2"></i>  Submit
                                </button>
                                <button type="submit" class="btn btn-shadow btn-info btn-sm w-100 rounded-0">  <i class="feather icon-download me-2"></i> Download Excel </button>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
	
		<!-- [ Main Content ] start -->
		<!-- Header and Footer fix table start -->
		<div class="col-sm-12">
			<div class="card">
				<div class="card-header d-flex align-items-center justify-content-between p-2">
					<h5>Kyc reports Details</h5>
					<input type="text" class="form-control form-control-sm w-auto" id="searchInput" placeholder="Search..." />
				</div>
				<div class="card-body p-0">
					<div class="dt-responsive table-responsive all-txn-kyc">
						<table id="header-footer-fix"
							class="table table-striped table-bordered nowrap">
							<thead>
								<tr>
									<th>Sr.No</th>
									<th>MID</th>
									<th>Merchant Name</th>
									<th>Product</th>
									<th>Billable</th>
									<th>Request_Date_Time</th>
									<th>Track Id</th>
									
									<th>Order Id</th>
									
									<th>response_message</th>
									
									<th>Status</th>
									<th>Signer Status</th>
								</tr>
							</thead>
							<tbody id="kycReport"></tbody>
						</table>

						<!-- pagination start -->

						<!-- pagination end -->
					</div>
				</div>
				<div class="kyc-pagination">
					<div class="pagination-inner">
						<div class="mb-0">
							<select class="form-select" id="exampleFormControlSelect1">
								<option>100</option>
								<option>150</option>
								<option>200</option>
								<option>250</option>
								<option>300</option>
							</select>
						</div>
						<nav aria-label="Page navigation example">
							<ul class="pagination justify-content-end"></ul>
						</nav>
					</div>
				</div>
			</div>
		</div>
		<!-- Header and Footer fix table end -->
		<!-- [ Main Content ] end -->
	</div>
</div>
<script>









$(document).ready(function () {
    let currentPage = 0;
    let pageSize = parseInt($('#exampleFormControlSelect1').val()) || 3; // Initialize with selected value
    let selectedProduct = '';
    let cachedData = {};
    let searchTriggered = false;

    // Handle page size changes
    $('#exampleFormControlSelect1').change(function() {
        pageSize = parseInt($(this).val()) || 3;
        currentPage = 0; // Reset to first page
        cachedData = {}; // Clear cache
        searchTriggered = false;
        
        // Only fetch if we have a valid product selected
        if (selectedProduct && selectedProduct !== 'selectproduct') {
            fetchData(currentPage);
        }
    });

    function fetchData(page) {
        let dateRange = $("#pc-date_range_picker-2").val();
        let [formDate, toDate] = dateRange.split(' to ').map(date => date.trim());
        let searchInput = $('#searchInput').val().trim().toLowerCase();
       // let selectemid = $('#midSelect').val();
        
        // Only update selectedProduct if not a search-triggered fetch
        if (!searchTriggered) {
            selectedProduct = $('#productSelect').val().toLowerCase().replace(/\s/g, '');
        }

        let requestData = {
            mid: '${merchant.mid}',
            formDate: formDate,
            toDate: toDate,
            product: selectedProduct,
            page: page,
            size: pageSize
        };

        if (searchInput) {
            requestData.search = searchInput;
        }

        // Only use cache if not searching and cache exists for this page
        if (cachedData[page] && !searchTriggered && !searchInput) {
            updateTable(cachedData[page].data);
            updatePagination(cachedData[page].pagination.totalPages, cachedData[page].pagination.currentPage);
            return;
        }

        $.ajax({
            url: '${url}/app/public/kyc/ui/Report/Merchant',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            beforeSend: function () {
            	ajaxindicatorstart('📊 Fetching report Please wait.');
    		    },
            success: function (response) {
                const actualData = response.data;
                const pagination = response.paginationResponse;
                
                if (response.statusCode === 404 || !actualData || actualData.length === 0) {
                    $('#kycReport').html('<tr><td colspan="11" style="text-align: center; color: red;">No Record found</td></tr>');
                    $('.pagination').html('');
                } else {
                    if (!searchTriggered && !searchInput) {
                        cachedData[page] = { data: actualData, pagination: pagination };
                    }
                    updateTable(actualData);
                    updatePagination(pagination.totalPages, pagination.currentPage);
                }

                searchTriggered = false;
            },
            error: function (xhr, status, error) {
                console.error("Error:", error);
                $('#kycReport').html('<tr><td colspan="11" style="text-align: center; color: red;">Error loading data</td></tr>');
                $('.pagination').html('');
                searchTriggered = false;
            },
            complete: function () {
                ajaxindicatorstop(); // ✅ Stop loader here after success or error
            }
        });
    }

   
    function updateTable(data) {
        let html = "";
        if (data && data.length > 0) {
            data.forEach(function (item, index) {
                html += "<tr>" +
                    "<td>" + ((currentPage * pageSize) + index + 1) + "</td>" +
                    "<td>" + (item.merchantId || '') + "</td>" +
                    "<td>" + (item.merchantName || '') + "</td>" +
                    "<td>" + (item.product || '') + "</td>" +
                    "<td>" + (item.billable || '') + "</td>" +
                    "<td>" + formatDate(item.merchantRequestAt) + "</td>" +
                    "<td>" + (item.trackId || '') + "</td>" +
					
					"<td>" + (item.orderId || '') + "</td>" +
                   
                    "<td>" + (item.responseMessage || '') + "</td>" +
                    // "<td><span style='color:" + (item.status === "SUCCESS" ? "green" : "red") + 
                    // "; font-weight: bold;'>" + (item.status || '') + "</span></td>" 
                    "<td><button class='btn " + 
                        (item.status === "SUCCESS" || item.status === "SIGNED"   ? "btn-success" : "btn-danger") + 
                        " btn-sm shadow-sm' style='color: #ffffff; font-size: 0.5rem; font-weight: bold;'>" + 
                        (item.status || '') + 
                        "</button></td>"
                        
                        "<td><button class='btn " + 
                        (item.signerStatus === "SUCCESS" || item.signerStatus === "INITIATED" ? "btn-success" : "btn-danger") + 
                        " btn-sm shadow-sm' style='color: #ffffff; font-size: 0.5rem; font-weight: bold;'>" + 
                        (item.signerStatus || '') + 
                        "</button></td>"
                    "</tr>";
            });
        } else {
            html = '<tr><td colspan="13" style="text-align: center; color: red;">No data available</td></tr>';
        }

        $('#kycReport').html(html);
    }

    function updatePagination(totalPages, currentPage) {
        let paginationHtml = '';

        // Previous button
        paginationHtml += '<li class="page-item' + (currentPage === 0 ? ' disabled' : '') + '">';
        paginationHtml += '<a class="page-link" href="javascript:void(0);" onclick="changePage(' + (currentPage - 1) + ')">Previous</a></li>';

        // Page numbers
        let startPage = Math.max(0, currentPage - 2);
        let endPage = Math.min(totalPages - 1, currentPage + 2);
        
        if (startPage > 0) {
            paginationHtml += '<li class="page-item"><a class="page-link" href="javascript:void(0);" onclick="changePage(0)">1</a></li>';
            if (startPage > 1) {
                paginationHtml += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
        }
        
        for (let i = startPage; i <= endPage; i++) {
            paginationHtml += '<li class="page-item' + (i === currentPage ? ' active' : '') + '">';
            paginationHtml += '<a class="page-link" href="javascript:void(0);" onclick="changePage(' + i + ')">' + (i + 1) + '</a></li>';
        }
        
        if (endPage < totalPages - 1) {
            if (endPage < totalPages - 2) {
                paginationHtml += '<li class="page-item disabled"><span class="page-link">...</span></li>';
            }
            paginationHtml += '<li class="page-item"><a class="page-link" href="javascript:void(0);" onclick="changePage(' + (totalPages - 1) + ')">' + totalPages + '</a></li>';
        }

        // Next button
        paginationHtml += '<li class="page-item' + (currentPage === totalPages - 1 ? ' disabled' : '') + '">';
        paginationHtml += '<a class="page-link" href="javascript:void(0);" onclick="changePage(' + (currentPage + 1) + ')">Next</a></li>';

        $('.pagination').html(paginationHtml);
    }

    window.changePage = function (page) {
        if (page >= 0 && page !== currentPage) {
            currentPage = page;
            fetchData(page);
        }
    }

    function formatDate(dateStr) {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        if (isNaN(date.getTime())) return '';

        const pad = n => (n < 10 ? '0' + n : n);

        return pad(date.getDate()) + "-" +
            pad(date.getMonth() + 1) + "-" +
            date.getFullYear() + " " +
            pad(date.getHours()) + ":" +
            pad(date.getMinutes()) + ":" +
            pad(date.getSeconds());
    }

    // Search input handler
    $('#searchInput').on('input paste', function() {
        let val = $(this).val().trim();
        if (val !== '') {
            searchTriggered = true;
            currentPage = 0;
            cachedData = {};
            fetchData(currentPage);
        } else if (selectedProduct && selectedProduct !== 'selectproduct') {
            // If search is cleared and we have a product selected, reload original data
            searchTriggered = false;
            currentPage = 0;
            cachedData = {};
            fetchData(currentPage);
        }
    }).keypress(function(e) {
        if (e.which === 13) { // Enter key
            let val = $(this).val().trim();
            if (val !== '') {
                searchTriggered = true;
                currentPage = 0;
                cachedData = {};
                fetchData(currentPage);
            }
        }
    });

    // Main fetch button handler
    $('#kycReport_Merchnat').click(function () {
        selectedProduct = $('#productSelect').val().toLowerCase().replace(/\s/g, '');
        
        if (!selectedProduct || selectedProduct === 'selectproduct') {
            $('#kycReport').html('<tr><td colspan="13" style="text-align:center;color:red;">Please select a valid product</td></tr>');
            $('.pagination').html('');
            return;
        }

        searchTriggered = false;
        currentPage = 0;
        cachedData = {};
        fetchData(currentPage);
    });
});

</script>
<%@ include file="MerchantFooter.jsp"%>
