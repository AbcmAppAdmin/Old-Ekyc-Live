
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>


<head>
<%@ include file="AdminHeader.jsp"%>
<meta charset="UTF-8">
<title>eKYC | Merchant Update</title>

</head>
<div class="pc-container">
	<div class="pc-content">
		<!-- [ breadcrumb ] start -->
		<div class="page-header">
			<div class="page-block">
				<div class="row align-items-center">
					<div class="col-md-12">
						<div class="page-header-title">
							<h2 class="mb-0">Merchant Onboard</h2>

						</div>
					</div>
				</div>
			</div>
		</div>
		<!-- [ breadcrumb ] end -->
		<form id="merchant_frm">
			<div class="row">
				<div class="col-md-4 col-12">
					<label for="merchantName" class="form-label"> Merchant Name
						<span style="color: red;">*</span>
					</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-user"></i></span> <input type="text" class="form-control"
							placeholder="Merchant Name" aria-label="Username" name="name"
							aria-describedby="basic-addon1" value="${merchant.name}">
						<input type="hidden" value="${merchant.id}" name="id">
						<input type="hidden" value="${username}" name="username">
						
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantEmail" class="form-label">Merchant
						Email <span style="color: red;">*</span>
					</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"> <i
							class="fas fa-envelope"></i>
						</span> <input type="text" class="form-control"
							placeholder="Enter Merchant Email" name="email"
							aria-label="Email" aria-describedby="basic-addon1"
							value="${merchant.email}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label"> Phone No <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">

						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-phone"></i></span> <input type="text" class="form-control"
							placeholder="Enter Merchant Phone No" aria-label="Phone"
							name="phone" aria-describedby="basic-addon1"
							value="${merchant.phoneOne}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">
						Alternative Mobile No <span style="color: red;">*</span>
					</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-phone"></i></span> <input type="text" class="form-control"
							placeholder="Enter Alternative Phone No" aria-label="username"
							aria-describedby="basic-addon1" name="alterNativePhone"
							value="${merchant.phoneTwo}">
					</div>
				</div>


				<div class="col-md-4 col-12 ">
					<label for="merchantPhoneNo" class="form-label">Business
						Type <span style="color: red;">*</span>
					</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-briefcase"></i></span> <select class="form-select"
							id="BusinessType" aria-label="Example select with button addon"
							name="businessType">
							<option value="">Select Business Type</option>
							<option value="Proprietorship">Proprietorship</option>
							<option value="Partnership">Partnership</option>
							<option value="LLP">Limited Liability Partnership (LLP)</option>
							<option value="PvtLtd">Private Limited Company (Pvt Ltd)</option>
							<option value="PublicLtd">Public Limited Company</option>
							<option value="OPC">One Person Company (OPC)</option>
							<option value="Society">Society</option>
							<option value="Trust">Trust</option>
							<option value="NGO">NGO</option>
							<option value="Section8">Section 8 Company</option>
							<option value="Cooperative">Co-operative Society</option>
							<option value="JV">Joint Venture</option>
							<option value="Franchise">Franchise</option>
							<option value="Foreign">Foreign Company</option>
							<option value="Others">Others</option>

						</select>
					</div>
				</div>


				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">Website Url</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-globe"></i></span> <input type="text" class="form-control"
							placeholder="Enter Website Url" name="websiteUrl"
							aria-label="Username" aria-describedby="basic-addon1"
							value="${merchant.websiteUrl}">
					</div>


				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">Business
						Address <span style="color: red;">*</span>
					</label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-building"></i></span> <input type="text"
							class="form-control" placeholder="Business Address"
							aria-label="Email" aria-describedby="basic-addon1"
							name="businessAddress" value="${merchant.buissnessAaddress}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">Country <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-globe"></i> </span> <input type="text"
							class="form-control" placeholder=" Enter Country"
							aria-label="Phone" name="country" aria-describedby="basic-addon1"
							value="${merchant.country}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">State <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-map-marker-alt"></i></span> <input type="text"
							class="form-control" placeholder=" Enter State"
							aria-label="username" name="state"
							aria-describedby="basic-addon1" value="${merchant.state}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">City <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fas fa-city"></i></span> <input type="text" class="form-control"
							placeholder=" Enter City" aria-label="Email"
							aria-describedby="basic-addon1" name="city"
							value="${merchant.city}">
					</div>
				</div>
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">Pincode <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fa fa-map-marker"></i></span> <input type="text"
							class="form-control" placeholder=" Enter PinCode"
							aria-label="Username" aria-describedby="basic-addon1"
							name="pincode" value="${merchant.pincode}">
					</div>
				</div>

				<div class="col-md-4 col-12">

					<label class="form-label">Select Products <span
						style="color: red;">*</span></label>
					<div class="dropdown" id="productsDropdown"
						style="border: 1px solid #ccc; padding: 8px; cursor: pointer; user-select: none; width: 100%; max-width: 300px;">
						Select Products</div>
					<div id="productsList"
						style="display: none; border: 1px solid #ccc; max-height: 150px; overflow-y: auto; padding: 8px; width: 100%; max-width: 300px; background: white; position: absolute; z-index: 1000;">
						<c:forEach var="product" items="${productList}">
							<c:set var="isChecked" value="false" />
							<c:forEach var="assigned" items="${productRoutes}">
								<c:if test="${assigned.product_id == product.id}">
									<c:set var="isChecked" value="true" />
								</c:if>
							</c:forEach>
							<input type="checkbox" name="products"
								data-product-id="${product.id}" value="${product.productName}"
								id="prod${product.id}" <c:if test="${isChecked}">checked</c:if>>
							<label for="prod${product.id}">${product.productName}</label>
							<br>
						</c:forEach>
					</div>

					<span id="products_msg" style="color: red;"></span>
				</div>
				
				<div class="col-md-4 col-12">
					<label for="merchantPhoneNo" class="form-label">Ip Allowed <span
						style="color: red;">*</span></label>
					<div class="input-group mb-3">
						<span class="input-group-text" id="basic-addon1"><i
							class="fa fa-map-marker"></i></span> <input type="text"
							class="form-control" placeholder=" Enter PinCode"
							aria-label="Username" aria-describedby="basic-addon1"
							name="ipAllowed" value="${merchant.ipAllowed}">
					</div>
				</div>
				<div style="text-align: center;">
					<span id="onboard_msg"></span>
				</div>

				<div class="bill-register mb-3 text-center">
					<button type="submit" class="btn btn-shadow btn-info mt-2">Update
						Merchant</button>
				</div>
			</div>
		</form>
	</div>
</div>
<%@ include file="AdminFooter.jsp"%>


<script>
$(document).ready(function () {
    $('#merchant_frm').submit(function (e) {
        e.preventDefault();
        const fields = {
            id: $('input[name="id"]').val().trim(),
            merchantName: $('input[name="name"]').val().trim(),
            merchantEmail: $('input[name="email"]').val().trim(),
            merchantPhone: $('input[name="phone"]').val().trim(),
            alterNativePhone: $('input[name="alterNativePhone"]').val().trim(),
            businessType: $('select[name="businessType"]').val(),
            businessAddress: $('input[name="businessAddress"]').val().trim(),
            country: $('input[name="country"]').val().trim(),
            state: $('input[name="state"]').val().trim(),
            city: $('input[name="city"]').val().trim(),
            pincode: $('input[name="pincode"]').val().trim(),
            websiteUrl: $('input[name="websiteUrl"]').val().trim(),
            username: $('input[name="username"]').val().trim(),
            ipAllowed: $('input[name="ipAllowed"]').val().trim()
            
        };

        const productRoutes = [];
        $('#productsList input[name="products"]:checked').each(function () {
            productRoutes.push({
                productId: $(this).data('product-id'),
                productName: $(this).val(),
                createdBy: fields.username
            });
        });

        if (productRoutes.length === 0) {
            showMessage('#products_msg', 'Please select at least one product.');
            return;
        }

        const requestBody = {
            id: fields.id,
            name: fields.merchantName,
            email: fields.merchantEmail,
            phone: fields.merchantPhone,
            alterNativePhone: fields.alterNativePhone,
            businessType: fields.businessType,
            websiteUrl: fields.websiteUrl,
            businessAddress: fields.businessAddress,
            country: fields.country,
            state: fields.state,
            city: fields.city,
            pincode: fields.pincode,
            createdBy: fields.username,
            ipAllowed: fields.ipAllowed,
            productRoutes: productRoutes
        };
        //alert(JSON.stringify(requestBody));
        $.ajax({
            url: '${url}/app/public/onboardMerchant',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(requestBody),
            success: function (response) {
                const msgColor = response.responseCode === 200 ? 'green' : 'red';
                $('#onboard_msg').text(response.message).css({ color: msgColor, fontWeight: 'bold' });

                if (response.responseCode === 200) {
                    setTimeout(() => $('#onboard_msg').fadeOut(500, function () {
                        $(this).empty().show();
                    }), 3000);
                }
            },
            error: function () {
                $('#onboard_msg').text('Error during onboarding!').css({ color: 'red', fontWeight: 'bold' });
                setTimeout(() => $('#onboard_msg').fadeOut(500, function () {
                    $(this).empty().show();
                }), 4000);
            }
        });
    });

    function showMessage(selector, message) {
        $(selector).text(message).css({ color: 'red', fontWeight: 'bold' });
    }

    // Product dropdown toggle
    const dropdown = document.getElementById('productsDropdown');
    const list = document.getElementById('productsList');

    dropdown.addEventListener('click', () => {
        list.style.display = (list.style.display === 'none' || list.style.display === '') ? 'block' : 'none';
    });

    document.addEventListener('click', function (event) {
        if (!dropdown.contains(event.target) && !list.contains(event.target)) {
            list.style.display = 'none';
        }
    });
});
</script>




