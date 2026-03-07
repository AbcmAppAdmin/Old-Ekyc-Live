<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<head>
<%@ include file="AdminHeader.jsp"%>
<meta charset="UTF-8">
<title>eKYC | Authntication</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>


<div class="pc-container">
	<div class="pc-content">
		<!-- [ breadcrumb ] start -->

		<div class="row">
			<div class="col-12">
				<div class="card table-card">
					<div
						class="card-header d-flex align-items-center justify-content-between py-3">
						<h5 class="mb-0">Authentication Info</h5>
					</div>
					<div class="card-body">
						<div class="table-responsive">
							<table class="table table-hover auth-table table-bordered"
								id="pc-dt-simple">
								<thead>
									<tr>
										<th>Serial No.</th>
										<th>Merchant ID</th>
										<th>Merchant Name</th>
										<th>UserName</th>
										<th>Create At</th>
										<th>OKYC</th>
										<th>Pan</th>
										<th>GST</th>
										<th>DRIVING_LICENSE</th>
										<th>VOTER-ID</th>
										<th>E-Sign-View</th>
										<th>Billing</th>
										<th>Dashboard</th>
										<th>Product</th>
										<th>App</th>
										<th>Kyc Report</th>
										<th>status</th>
									</tr>
								</thead>
								<tbody id="AuthData">

								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- [ Main Content ] end -->
	</div>
</div>
<!-- [ Main Content ] end -->

<%@ include file="AdminFooter.jsp"%>


<script>
        
        
$(document).ready(function () {
    $.ajax({
        url: '${url}' + '/app/public/allMerchnat',
        type: 'GET',
        success: function (response) {
            var html = "";
            if (response.responseCode == 200) {
                var Data = response.data;
                console.log("Response: " + JSON.stringify(Data));
                Data.forEach(function (item, index) {
                    let Aadharstatuschecked = item.aadharOkyc == "ENABLE" ? "checked" : "";
                    let Panprostatuschecked = item.panPro == "ENABLE" ? "checked" : "";
                    let GstLitstatuschecked = item.gstLit == "ENABLE" ? "checked" : "";
                    let Dlstatuschecked = item.drivingLicense == "ENABLE" ? "checked" : "";
                    let Voterstatuschecked = item.voterid == "ENABLE" ? "checked" : "";
                    let Esignstatuschecked = item.esign == "ENABLE" ? "checked" : "";
                    let Billingstatuschecked = item.billing == "ENABLE" ? "checked" : "";
                    let Productstatuschecked = item.product == "ENABLE" ? "checked" : "";
                    let Dashboardstatuschecked = item.dashboard == "ENABLE" ? "checked" : "";
                    let Appsstatuschecked = item.apps == "ENABLE" ? "checked" : "";
                    let KycReportstatuschecked = item.kycReport == "ENABLE" ? "checked" : "";
                    let MerchantStatuschecked = item.status === true ? "checked" : "";
                    html += "<tr>";
                    html += "<td>" + (index + 1) + "</td>";
                    html += "<td>" + item.mid + "</td>";
                    html += "<td>" + item.name + "</td>";
                    html += "<td>" + item.credentials.username + "</td>";
                    html += "<td>" + formatDateTimeAmPm(item.credentials.createdAt) + "</td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','aadharOkyc',this.value,'String')\" value='" + (Aadharstatuschecked ? "DESABLE" : "ENABLE") + "' " + Aadharstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','panPro',this.value,'String')\" value='" + (Panprostatuschecked ? "DESABLE" : "ENABLE") + "' " + Panprostatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','gstLit',this.value,'String')\" value='" + (GstLitstatuschecked ? "DESABLE" : "ENABLE") + "' " + GstLitstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','drivingLicense',this.value,'String')\" value='" + (Dlstatuschecked ? "DESABLE" : "ENABLE") + "' " + Dlstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','voterId',this.value,'String')\" value='" + (Voterstatuschecked ? "DESABLE" : "ENABLE") + "' " + Voterstatuschecked + "></td>";
                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','esign',this.value,'String')\" value='" + (Esignstatuschecked ? "DESABLE" : "ENABLE") + "' " + Esignstatuschecked + "></td>";


                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','billing',this.value,'String')\" value='" + (Billingstatuschecked ? "DESABLE" : "ENABLE") + "' " + Billingstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','dashboard',this.value,'String')\" value='" + (Dashboardstatuschecked ? "DESABLE" : "ENABLE") + "' " + Dashboardstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','product',this.value,'String')\" value='" + (Productstatuschecked ? "DESABLE" : "ENABLE") + "' " + Productstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','apps',this.value,'String')\" value='" + (Appsstatuschecked ? "DESABLE" : "ENABLE") + "' " + Appsstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','kycReport',this.value,'String')\" value='" + (KycReportstatuschecked ? "DESABLE" : "ENABLE") + "' " + KycReportstatuschecked + "></td>";

                    html += "<td><input type='checkbox' onclick=\"onclickActivity('" + item.mid + "','" + item.id + "','" + item.credentials.username + "','status',this.checked,'Boolean')\" " + MerchantStatuschecked + "></td>";

                    html += "</tr>";
                });

                $("#AuthData").html(html);
            }
        },
        error: function (xhr, status, error) {
            console.log('Error: ' + error);
        }
    });
});



function formatDateTimeAmPm(dateStr) {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    if (isNaN(date.getTime())) return '';

    const pad = n => (n < 10 ? '0' + n : n);
    let hours = date.getHours();
    const minutes = pad(date.getMinutes());
    const seconds = pad(date.getSeconds());
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12 || 12; // Convert to 12-hour format

    return pad(date.getDate()) + '-' + pad(date.getMonth() + 1) + '-' + date.getFullYear() + ' ' +
           pad(hours) + ':' + minutes + ':' + seconds + ' ' + ampm;
}

        
        function onclickActivity(mid,id,username,columnName,value,dataType) {
            let urls = '${url}'+'/app/public/update-authstatus';
            // Fixed request body
            let data = {
                mid: mid,
                id: id,
                username: username,
                columnName: columnName,
                value: value,
                dataType: dataType
            };
            
           // alert(JSON.stringify(data));

            $.ajax({
                type: "POST",
                url: urls,
                data: JSON.stringify(data),
                async: true,
                contentType: "application/json",
                beforeSend: function(xhr) {
                    // Optionally handle before the request
                },
                success: function(response) { 
                    console.log(response);
                },
                error: function(xhr, status, error) { 
                    console.log("error:" + error); 
                }
            });
        }
 
        </script>
