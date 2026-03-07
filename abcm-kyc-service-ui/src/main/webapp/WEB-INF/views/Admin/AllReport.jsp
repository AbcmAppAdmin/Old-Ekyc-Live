<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<head>
<%@ include file="AdminHeader.jsp"%>
<meta charset="UTF-8">
<title>eKYC | TxnReport</title>
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
							<h2 class="mb-0">eKYC-Transaction Reports</h2>
						</div>
					</div>
				</div>
			</div>
		</div>
		<form action="${url}/app/public/downloadTxnReport" method="post">
  <div class="row mb-3">
    <!-- Date Range with Label -->
    <div class="col-md-4 col-12">
        <label for="pc-date_range_picker-2" class="form-label">Select Date Range</label>
        <div class="input-group">
            <span class="input-group-text"><i class="feather icon-calendar"></i></span>
            <input type="text" id="pc-date_range_picker-2" class="form-control" placeholder="Select date range" name="dateRange" />
        </div>
        <!-- Show range here manually -->
        <small id="date-range-label" style="color: green; font-weight: bold;"></small>
    </div>

    <!-- MID Select with Label -->
    <div class="col-md-4 col-12">
      <label for="midSelect" class="form-label">Select MID</label>
      <select class="form-select form-select w-100" id="midSelect" name="mid">
       <option value="">All Merchants</option> <!-- All merchants option -->
        <c:forEach var="merchant" items="${merchantList}">
        
          <option value="${merchant.mid}">${merchant.mid}-${merchant.name}</option>
        </c:forEach>
      </select>
    </div>

    <!-- Product Select with Label -->
    <div class="col-md-4 col-12">
      <label for="productSelect" class="form-label">Select Product</label>
      <select class="form-select form-select w-100" id="productSelect" name="product">
       <option value="allproduct" selected="selected">All Product</option>
        <option value="OKYC" >OKYC</option>
        <option value="Pan">Pan</option>
        <option value="Gstin">GSTIN</option>
         <option value="DRIVING_LICENSE">DRIVING_LICENSE</option>
         <option value="voter-id">VOTER-ID</option>
          <option value="e-sign">E-SIGN</option>
      </select>
    </div>
    
    <div class="col-12">
  <div class="d-flex justify-content-center gap-3 my-3">
    <button type="button" id="showReport" class="btn btn-shadow btn-info">
      <i class="fa fa-save me-2"></i> Submit
    </button>
    <button type="submit" class="btn btn-shadow btn-info">
      <i class="feather icon-download me-2"></i> Download Excel
    </button>
  </div>
</div>
    </div>
    </form>
 

		<!-- [ Main Content ] start -->
		<!-- Header and Footer fix table start -->
		<div class="col-sm-12">
			<div class="card">
				<div
					class="card-header d-flex align-items-center justify-content-between"
					style="padding: 0.75rem 1.72rem;">
					<h5>Kyc Reports Details</h5>
					    <input type="text" class="form-control form-control-sm w-auto" id="searchInput" placeholder="Search..." />

				</div>
				
				
				
				
				<div class="card-body">
					<div class="dt-responsive table-responsive">
						<table id="header-footer-fix"
							class="table table-striped table-bordered nowrap">
							<thead>
								<tr>
									<th>Sr.No</th>
									<th>MID</th>
									<th>Merchant Name</th>
									<th>Provider Name</th>
									<th>Product</th>
										<th>Billable</th>
									<th>Identification No</th>
									<th>Customer Name</th>
									<th>Legal Name</th>
								
									<th>Created At
									<th>Request_At</th>
									<th>Response_At</th>
									<th>Track Id</th>
									<th>Reference Id</th>
									
									<th>Order Id</th>
																		
								
									<th>response_message</th>
									
									<th>Status</th>
											<th>Signer Status</th>
									<th>provider_response_message</th>
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
						<div id="totalRecordsText" class="text-muted">
						        <!-- Total Records: 0 -->
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



<%@ include file="AdminFooter.jsp"%>

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
        let selectemid = $('#midSelect').val();
        
        // Only update selectedProduct if not a search-triggered fetch
        if (!searchTriggered) {
            selectedProduct = $('#productSelect').val().toLowerCase().replace(/\s/g, '');
        }

        let requestData = {
            mid: selectemid,
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
        console.log("request Body"+JSON.stringify(requestData))
        $.ajax({
            url: '${url}/app/public/kyc/ui/Report',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestData),
            beforeSend: function () {
            	ajaxindicatorstart('📊 Fetching report Please wait.');
  		    },
            success: function (response) {
                const actualData = response.data;
              console.log("actualData"+JSON.stringify(response));
                const pagination = response.paginationResponse;
				
				const totalRecords = pagination.totalElements || 0;
				    const startRecord = (currentPage * pageSize) + 1;
				    const endRecord = Math.min((currentPage + 1) * pageSize, totalRecords);

				    $('#totalRecordsText').text(
				        "Showing " + startRecord + " To " + endRecord + " Of " + totalRecords + " Records"
				    );
                
                if (response.statusCode === 404 || !actualData || actualData.length === 0) {
                    $('#kycReport').html('<tr><td colspan="16" style="text-align: center; color: red;">No Record found</td></tr>');
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
                ajaxindicatorstop();
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
                    "<td>" + (item.providerName || '') + "</td>" +
                    "<td>" + (item.product || '') + "</td>" +
                    "<td>" + (item.billable || '') + "</td>" + 
                    "<td>" + maskIdentificationNumber(item.identificationNo, item.product) + "</td>" +
                    "<td>" + ((item.customerName || '').toUpperCase()) + "</td>"+
                    "<td>" + (item.legalName || '') + "</td>" +
                    "<td>" + formatDate(item.createdAt) + "</td>" +
                    "<td>" + formatDate(item.merchantRequestAt) + "</td>" +
                    "<td>" + formatDate(item.merchantResponseAt) + "</td>" +
                    "<td>" + (item.trackId || '') + "</td>" +
                    "<td>" + (item.requestId || '') + "</td>" +
					"<td>" + (item.orderId || '') + "</td>" +
					                   


                   
                    "<td>" + (item.responseMessage || '') + "</td>" +
                    
                    "<td><span style='color:" + (item.status === "SUCCESS" || item.status === "SIGNED" ? "green" : "red") + 
                    "; font-weight: bold;'>" + (item.status || '') + "</span></td>"+
           
                    "<td><span style='color:" + (item.signerStatus === "SUCCESS" || item.signerStatus === "INITIATED" ? "green" : "red") + 
                    "; font-weight: bold;'>" + (item.signerStatus || '') + "</span></td>"+
                    "<td>" + (item.reasonMessage || '') + "</td>" +
                    "</tr>";
            });
        } else {
            html = '<tr><td colspan="13" style="text-align: center; color: red;">No data available</td></tr>';
        }

        $('#kycReport').html(html);
    }

    
    
    
    function maskIdentificationNumber(identificationNo, product) {
        if (!identificationNo) return '';
        if (product && product.toUpperCase() === 'GSTIN') {
            if (identificationNo.length = 15) {
                return identificationNo.substring(0, 7) + 'XXXXXXX';
            }
            return identificationNo;
        } 
        else if (product && product.toUpperCase() === 'PAN') {
            if (identificationNo.length==10) {
                return identificationNo.substring(0, 5) + 'XXXXX';
            }
            return identificationNo;
        }
        
        else if (product && product.toUpperCase() === 'OKYC') {
            if (identificationNo.length === 12) {
                return 'XXXXXXXX' + identificationNo.slice(-4);
            }
           
            return identificationNo;
        }
        return identificationNo;
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
    $('#showReport').click(function () {
    	//alert("call when click")
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



