<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ include file="AdminHeader.jsp"%>
<head>
<meta charset="UTF-8">
<title>eKYC | TxnReport</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<div class="pc-container">
    <div class="dh-350"></div>
    <div class="pc-content neg-h">
        <div class="page-header">
            <div class="page-block">
                <div class="row align-items-center">
                    <div class="col-md-12">
                        <div class="page-header-title">
                            <h2 class="mb-0">E-KYC Signer Report</h2>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <form action="${url}/app/public/downloadTxnReport" method="post">
            <div class="row mb-3">
                <div class="col-md-4 col-12">
                    <label for="pc-date_range_picker-2" class="form-label">Select Date Range</label>
                    <div class="input-group">
                        <span class="input-group-text"><i class="feather icon-calendar"></i></span>
                        <input type="text" id="pc-date_range_picker-2" class="form-control" placeholder="Select date range" name="dateRange" />
                    </div>
                    <small id="date-range-label" style="color: green; font-weight: bold;"></small>
                </div>

                <div class="col-md-4 col-12">
                    <label for="midSelect" class="form-label">Select MID</label>
                    <select class="form-select form-select w-100" id="midSelect" name="mid">
                        <option value="">All Merchants</option>
                        <c:forEach var="merchant" items="${merchantList}">
                            <option value="${merchant.mid}">${merchant.mid}-${merchant.name}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-4 col-12">
                    <label for="productSelect" class="form-label">Select Status</label>
                    <select class="form-select form-select w-100" id="productSelect" name="product">
                        <option value="ALL" selected="all">All</option>
                        <option value="SIGNED">SIGNED</option>
                        <option value="INPROGRESS">INPROGRESS</option>
                        <option value="INITIATED">INITIATED</option>
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

        <div class="col-sm-12">
            <div class="card">
                <div class="card-header d-flex align-items-center justify-content-between" style="padding: 0.75rem 1.72rem;">
                    <h5>Kyc Reports Details</h5>
                    <input type="text" class="form-control form-control-sm w-auto" id="searchInput" placeholder="Search..." />
                </div>
                <div class="card-body">
                    <div class="dt-responsive table-responsive">
                        <table id="header-footer-fix" class="table table-striped table-bordered nowrap">
                            <thead>
                                <tr>
                                    <th>Sr.No</th>
                                    <th>MID</th>
                                    <th>Merchant Name</th>
                                    <th>Provider Name</th>
                                    <th>Order ID</th>
                                    <th>Track ID</th>
                                    <th>Requested At</th>
                                    <th>Signed At</th>
                                    <th>Document Path</th>
                                    <th>Signer Document Path</th>
                                    <th>Status</th>
                                    <th>Signer Status</th>
                                </tr>
                            </thead>
                            <tbody id="kycReport"></tbody>
                        </table>
                    </div>
                </div>

                <div class="kyc-pagination">
                    <div class="pagination-inner">
                        <div class="mb-0">
                            <select class="form-select" id="exampleFormControlSelect1">
                                <option>20</option>
                                <option>50</option>
                                <option>100</option>
                            </select>
                        </div>
                        <div id="totalRecordsText" class="text-muted"></div>
                        <nav aria-label="Page navigation example">
                            <ul class="pagination justify-content-end"></ul>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="AdminFooter.jsp"%>

<script>
$(document).ready(function () {
    var currentPage = 0;
    var pageSize = parseInt($('#exampleFormControlSelect1').val()) || 20;
    var cachedData = {};
    var searchTriggered = false;
    var selectedProduct = "";

    function fetchData(page) {
        var dateRange = $("#pc-date_range_picker-2").val();
        var dates = dateRange.split(" to ");
        var fromDate = dates[0] || "";
        var toDate = dates[1] || "";
        var searchInput = $('#searchInput').val().trim().toLowerCase();
        var selectemid = $('#midSelect').val();

        if (!searchTriggered) {
            selectedProduct = $('#productSelect').val();
        }

        // Build query parameters for GET request
        var queryParams = $.param({
            merchantId: selectemid || "",
            fromDate: fromDate,
            toDate: toDate,
            status: selectedProduct ? selectedProduct.toUpperCase() : "ALL",
            page: page,
            size: pageSize
        });

        // Append search if exists (optional, depends on backend)
        if (searchInput) {
            queryParams += "&search=" + encodeURIComponent(searchInput);
        }

        $.ajax({
            url: '${url}/app/public/report?' + queryParams,
            type: 'GET',
            beforeSend: function () {
                ajaxindicatorstart('📊 Fetching report Please wait.');
            },
            success: function (response) {
              
                var actualData = [];
                if (response.responseCode === 200 && response.data) {
                    var parsed = JSON.parse(response.data);
                    actualData = parsed.data.items || [];
                    var pagination = parsed.data.meta || {};
                    var totalRecords = pagination.totalElements || 0;
                    var startRecord = (currentPage * pageSize) + 1;
                    var endRecord = Math.min((currentPage + 1) * pageSize, totalRecords);
                    $('#totalRecordsText').text("Showing " + startRecord + " To " + endRecord + " Of " + totalRecords + " Records");
                    if (actualData.length === 0) {
                        $('#kycReport').html('<tr><td colspan="12" style="text-align:center;color:red;">No Record found</td></tr>');
                        $('.pagination').html('');
                    } else {
                        if (!searchTriggered && !searchInput) {
                            cachedData[page] = { data: actualData, pagination: pagination };
                        }
                        updateTable(actualData);
                        updatePagination(pagination.totalPages || 1, pagination.page || 0);
                    }
                } else {
                    $('#kycReport').html('<tr><td colspan="12" style="text-align:center;color:red;">No Record found</td></tr>');
                    $('.pagination').html('');
                }
                searchTriggered = false;
            },
            error: function(xhr, textStatus, errorThrown) {
                console.log("Error Response: ", xhr.responseText);
                console.log("Error Status: ", xhr.status);
                console.log("Error Thrown: ", errorThrown);

                if (xhr.status === 404) {
                    $('#kycReport').html('<tr><td colspan="12" style="text-align:center;color:red;">No data found</td></tr>');
                } else {
                    $('#kycReport').html('<tr><td colspan="12" style="text-align:center;color:red;">Error loading data. Please try again later.</td></tr>');
                }
                ajaxindicatorstop();
                $('.pagination').html('');
                searchTriggered = false;
                
            }
,
            complete: function () {
                ajaxindicatorstop();
            }
        });
    }

    function updateTable(items) {
        var html = "";
        for (var i = 0; i < items.length; i++) {
            var item = items[i];
            html += "<tr>" +
                "<td>" + ((currentPage * pageSize) + i + 1) + "</td>" +
                "<td>" + (item.merchantId || "") + "</td>" +
                "<td>" + (item.merchantName || "") + "</td>" +
                "<td>" + (item.provider || "") + "</td>" +
                "<td>" + (item.orderId || "") + "</td>" +
                "<td>" + (item.trackId || "") + "</td>" +
                "<td>" + formatDate(item.requestAt) + "</td>" +
                "<td>" + formatDate(item.signedAt) + "</td>" +
                "<td>" + (item.documentPath || "") + "</td>" +
                "<td>" + (item.signerDocumentPath || "") + "</td>" +
                "<td style='color:" + (item.status === "SIGNED" ? "green" : "orange") + "'>" + (item.status || "") + "</td>" +
                "<td>" + (item.signerStatus || "") + "</td>" +
                "</tr>";
        }
        $('#kycReport').html(html);
    }

    function updatePagination(totalPages, currentPageIndex) {
        var html = "";

        html += "<li class='page-item" + (currentPageIndex === 0 ? " disabled" : "") + "'>" +
                "<a class='page-link' href='javascript:void(0);' onclick='changePage(" + (currentPageIndex - 1) + ")'>Previous</a></li>";

        for (var i = 0; i < totalPages; i++) {
            html += "<li class='page-item" + (i === currentPageIndex ? " active" : "") + "'>" +
                    "<a class='page-link' href='javascript:void(0);' onclick='changePage(" + i + ")'>" + (i + 1) + "</a></li>";
        }

        html += "<li class='page-item" + (currentPageIndex === totalPages - 1 ? " disabled" : "") + "'>" +
                "<a class='page-link' href='javascript:void(0);' onclick='changePage(" + (currentPageIndex + 1) + ")'>Next</a></li>";

        $('.pagination').html(html);
    }

    window.changePage = function(page) {
        if (page >= 0 && page !== currentPage) {
            currentPage = page;
            fetchData(page);
        }
    }

    function formatDate(dateStr) {
        if (!dateStr) return "";
        var date = new Date(dateStr);
        if (isNaN(date.getTime())) return "";
        var pad = function(n) { return n < 10 ? "0" + n : n; };
        return pad(date.getDate()) + "-" + pad(date.getMonth() + 1) + "-" + date.getFullYear() + " " +
               pad(date.getHours()) + ":" + pad(date.getMinutes()) + ":" + pad(date.getSeconds());
    }

    $('#searchInput').on('input paste', function() {
        var val = $(this).val().trim();
        if (val !== "") {
            searchTriggered = true;
            currentPage = 0;
            cachedData = {};
            fetchData(currentPage);
        } else if (selectedProduct && selectedProduct !== "") {
            searchTriggered = false;
            currentPage = 0;
            cachedData = {};
            fetchData(currentPage);
        }
    }).keypress(function(e) {
        if (e.which === 13) {
            var val = $(this).val().trim();
            if (val !== "") {
                searchTriggered = true;
                currentPage = 0;
                cachedData = {};
                fetchData(currentPage);
            }
        }
    });

    $('#showReport').click(function() {
        selectedProduct = $('#productSelect').val();
        if (!selectedProduct || selectedProduct === "") {
            $('#kycReport').html('<tr><td colspan="12" style="text-align:center;color:red;">Please select a valid product</td></tr>');
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
