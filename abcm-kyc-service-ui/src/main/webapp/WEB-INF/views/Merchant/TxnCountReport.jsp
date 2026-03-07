<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<head>
<%@ include file="MerchantHeader.jsp"%>
<meta charset="UTF-8">
<title>eKYC | Transaction Count</title>
<style type="text/css">
#kycReportTableBody {
	font-size: 13px;
}
</style>
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
							<h2 class="mb-0">Transaction Count</h2>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="card tt-k mt-2">
			<div class="card-body p-3">
				<form action="${url}/app/public/downloadKycCount" method="post">
					<div class="row mb-3">
						<!-- Date Range with Label -->
						<div class="col-md-4 col-12">
							<label for="pc-date_range_picker-2" class="form-label">Select
								Date Range</label>
							<div class="input-group">
								<span class="input-group-text"><i class="feather icon-calendar"></i></span> 
								<input type="text" id="pc-date_range_picker-2" class="form-control" placeholder="Select date range" name="dateRange" />
							</div>
						</div>


						<div class="col-md-4 col-12">
							<label for="midSelect" class="form-label">Select MID</label> <select
								class="form-select form-select w-100" id="midSelect" name="mid">
								<option value="${merchant.mid}" selected="selected">${merchant.mid}</option>
							</select>
						</div>

						<!-- Product Select with Label -->
						<div class="col-md-4 col-12">
							<label for="productSelect" class="form-label">Select
								Product</label> <select class="form-select" id="productSelect"
								name="product">
								<option value="">All Product</option>
								<c:if
									test="${not empty merchant && merchant.aadharOkyc=='ENABLE'}">
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
                                
                                  <option value="E-SIGN">E-SIGN</option>

							</select>
						</div>

						<div class="col-12">
							<div class="d-flex justify-content-center gap-3 mt-3">
								<button type="button" id="fetchKycBtn" class="btn btn-shadow btn-info btn-sm rounded-0"> <i class="fa fa-save me-2"></i> Submit </button>
								<button type="submit" class="btn btn-shadow btn-info btn-sm rounded-0"> <i class="feather icon-download me-2"></i> Download Excel  </button>
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
					<h5>Successfull Transaction Count</h5>
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
									<th>OKYC</th>
									<th>PAN</th>
									<th>GSTIN</th>
									<th>DRIVING_LICENSE </th>
								<th>VOTER-ID </th>
								<th>E-SIGN</th>
									<th>Total</th>
								</tr>
							</thead>
							<tbody id="kycReportTableBody"></tbody>
						</table>

						<!-- pagination start -->

						<!-- pagination end -->
					</div>
				</div>

			</div>
		</div>

		<!-- Header and Footer fix table end -->
		<!-- [ Main Content ] end -->

	</div>

	<script>
$(document).ready(function () {

    // Call on page load
    fetchKycReport(getParams());

    // Call on button click
    $('#fetchKycBtn').click(function () {
        fetchKycReport(getParams());
    });
    function getParams() {
        let dateRange = $("#pc-date_range_picker-2").val();
        let [formDate, toDate] = dateRange.split(' to ').map(date => date.trim());
        let selectemid = $('#midSelect').val();
        let selectedProduct = $('#productSelect').val().toLowerCase().replace(/\s/g, '');
        return {
            fromDate: formDate,
            toDate: toDate,
            merchantId: selectemid,
            product: selectedProduct
        };
    }

    function fetchKycReport(params) {
    	ajaxindicatorstart('📊 Fetching Txn Count... Please wait.');
        $.ajax({
            url: '${url}'+'/app/public/kyc-count-report',
            type: 'GET',
            data: params,
            success: function (response) {
                console.log("KYC Report:", JSON.stringify(response));
                
                let html = "";
                if(response && response.data && response.data.data && response.data.data.length > 0){
                    response.data.data.forEach(function(item, index){
                        html += "<tr>" +
                                    "<td>" + (index + 1) + "</td>" +          // Sr. No. column
                                    "<td>" + item.merchantId + "</td>" +
                                    "<td>" + item.merchantName + "</td>" +
                                    "<td>" + item.okycCount + "</td>" +
                                    "<td>" + item.panCount + "</td>" +
                                    "<td>" + item.gstinCount + "</td>" +
                                    "<td>" + item.driving_license + "</td>" +
                                    "<td>" + item.voter_id + "</td>" +
                                    "<td>" + item.esign + "</td>" +
                                    "<td>" + item.totalCount + "</td>" +
                                "</tr>";
                    });
                } else {
                	html = "<tr><td colspan='10' style='text-align: center; font-weight: bold; color: red;'>No data found</td></tr>";

                }

                $('#kycReportTableBody').html(html);
                ajaxindicatorstop();
            },
            error: function (xhr) {
                console.error("Error fetching report:", xhr);
                $('#kycReportTableBody').html("<tr><td td colspan='5' style='text-align: center; font-weight: bold; color: red;>Error fetching data</td></tr>");
                ajaxindicatorstop();  
            }
        });
    }

});

</script>


	<%@ include file="MerchantFooter.jsp"%>