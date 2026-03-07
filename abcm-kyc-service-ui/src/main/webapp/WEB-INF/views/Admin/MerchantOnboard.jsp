<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<head>
<%@ include file="AdminHeader.jsp"%>
<meta charset="UTF-8">
<title>Merchant Onboarding</title>
</head>
<div class="pc-container">
	<div class="pc-content">
		<div class="card">
			<div class="card-header p-2">
				<h5 class="mb-0">Merchant Details</h5>
			</div>
			<div class="card-body">
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
									aria-describedby="basic-addon1">
							</div>
							<span id="name_msg"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantEmail" class="form-label">Merchant
								Email <span style="color: red;">*</span>
							</label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"> <i
									class="fas fa-envelope"></i>
								</span> <input type="text" class="form-control" id="merchantEmail"
									placeholder="Enter Merchant Email" name="email"
									aria-label="Email" aria-describedby="basic-addon1">
									
							</div>
							<span id="email_msg"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label"> Phone No <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-phone"></i></span> <input type="text" class="form-control"
									placeholder="Enter Merchant Phone No" aria-label="Phone"
									name="phone" aria-describedby="basic-addon1">
							</div>
							<span id="phone_msg"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Alternative
								Mobile No</label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-phone"></i></span> <input type="text" class="form-control"
									placeholder="Enter Alternative Phone No" aria-label="username"
									aria-describedby="basic-addon1" name="alterNativePhone">
							</div>
						</div>
						<div class="col-md-4 col-12 ">
							<label for="merchantPhoneNo" class="form-label">Business Type <span style="color: red;">*</span> </label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-briefcase"></i></span> 
									<select class="form-select" id="inputGroupSelect03" aria-label="Example select with button addon" name="businessType">
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
							<span id="business_type_msg" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Website Url</label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-globe"></i></span> <input type="text" class="form-control"
									placeholder="Enter Website Url" name="websiteUrl"
									aria-label="Username" aria-describedby="basic-addon1">
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
									name="businessAddress">
							</div>
							<span id="businessAddress" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Country <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-globe"></i> </span> <input type="text"
									class="form-control" placeholder=" Enter Country"
									aria-label="Phone" name="country" aria-describedby="basic-addon1">
							</div>
							<span id="country" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">State <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-map-marker-alt"></i></span> <input type="text"
									class="form-control" placeholder=" Enter State"
									aria-label="username" name="state"
									aria-describedby="basic-addon1">
							</div>
							<span id="state" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">City <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-city"></i></span> <input type="text" class="form-control"
									placeholder=" Enter City" aria-label="Email"
									aria-describedby="basic-addon1" name="city">
							</div>
							<span id="city" style="color: red;"></span>
						</div>

						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Pincode <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fa fa-map-marker"></i></span> <input type="text"
									class="form-control" placeholder=" Enter PinCode"
									aria-label="Username" aria-describedby="basic-addon1"
									name="pincode">
									<input type="hidden" name="createdBy" id="username" value="${username}">
									
							</div>
							
							<span id="pincode" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">

							<label class="form-label">Select Products <span style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i class="fas fa-briefcase"></i></span> 
								<div class="dropdown form-select" id="productsDropdown">
									Select Products
								</div>
								<div id="productsList" class=""  style="display: none; border: 1px solid #ccc; max-height: 150px; overflow-y: auto; padding: 8px; width: 100%; max-width: 300px; background: white; position: absolute; z-index: 1000;">
									<c:forEach var="product" items="${productList}">
										<input type="checkbox" name="products"
											data-product-id="${product.id}" value="${product.productName}"
											id="prod${product.id}">
										<label for="prod${product.id}">${product.productName}</label>
										<br>
									</c:forEach>
								</div>
							</div>
							<span id="products_msg" style="color: red;"></span>
						</div>


						<!-- PAN Rate input -->





						<!-- <div class="col-md-4 col-12" id="panRateContainer">
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fas fa-sort-amount-up"></i></span> <input type="tex"
									class="form-control" placeholder="Enter Gst Advance Rate" name="GstadvanceRate">
							</div>
						</div> -->


						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">UserName <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"> <i
									class="fa fa-user"></i></span> <input type="text" class="form-control"
									placeholder="Enter Merchant Username" aria-label="Email"
									aria-describedby="basic-addon1" name="username">
							</div>

							<span id="musername" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Password <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fa fa-lock"></i></span> <input type="text" class="form-control"
									placeholder="Enter Merchant Password" aria-label="Phone"
									aria-describedby="basic-addon1" name="password">
							</div>
							<span id="mpassword" style="color: red;"></span>
						</div>
						<div class="col-md-4 col-12">
							<label for="merchantPhoneNo" class="form-label">Ip Allowed <span
								style="color: red;">*</span></label>
							<div class="input-group mb-3">
								<span class="input-group-text" id="basic-addon1"><i
									class="fa fa-lock"></i></span> <input type="text" class="form-control"
									placeholder="Enter allowed ip" aria-label="Phone"
									aria-describedby="basic-addon1" name="ipAllowed">
							</div>
							<span id="mpassword" style="color: red;"></span>
						</div>
						<div style="text-align: center;">
							<span id="onboard_msg"></span>
						</div>


						<div class="bill-register mb-3 text-center">

							<button type="submit" class="btn btn-shadow btn-info mt-2 rounded-0">Save Merchant</button>
						</div>
					</div>
				</form>
			</div>
		</div>
		
	</div>
</div>

<%@ include file="AdminFooter.jsp"%>

<script>
$(document).ready(function () {
  $('#merchant_frm').submit(function (e) {
    e.preventDefault();
   // alert("Call onboard function");
    const fields = {
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
      username: $('input[name="username"]').val().trim(),
      password: $('input[name="password"]').val().trim(),
      ipAllowed: $('input[name="ipAllowed"]').val().trim(),
      websiteUrl: $('input[name="websiteUrl"]').val().trim(),
      createdBy: $('input[name="createdBy"]').val().trim()
     
    };

    // Validation for required fields
    const validations = [
      { field: fields.merchantName, selector: '#name_msg', message: 'Merchant Name is required.' },
      { field: fields.merchantEmail, selector: '#email_msg', message: 'Email is required.' },
      { field: fields.merchantPhone, selector: '#phone_msg', message: 'Merchant Phone no is required.' },
      { field: fields.businessType, selector: '#business_type_msg', message: 'Business Type is required.' },
      { field: fields.businessAddress, selector: '#businessAddress', message: 'Business Address is required.' },
      { field: fields.country, selector: '#country', message: 'Country is required.' },
      { field: fields.state, selector: '#state', message: 'State is required.' },
      { field: fields.city, selector: '#city', message: 'City is required.' },
      { field: fields.pincode, selector: '#pincode', message: 'Pincode is required.' },
      { field: fields.username, selector: '#musername', message: 'Username is required.' },
      { field: fields.password, selector: '#mpassword', message: 'Password is required.' }
    ];

    let hasError = false;
    // Clear all error messages
    validations.forEach(v => $(v.selector).empty());

    // Show error messages for empty fields
    validations.forEach(v => {
      if (!v.field) {
        showMessage(v.selector, v.message);
        hasError = true;
      }
    });

    // Collect selected products
   const productRoutes = [];
$('#productsList input[name="products"]:checked').each(function () {
  productRoutes.push({
    productId: $(this).data('product-id'),
    productName: $(this).val(),
    createdBy: fields.createdBy
  });
});

    if (productRoutes.length === 0) {
      showMessage('#products_msg', 'Please select at least one product.');
      hasError = true;
    }

    // If errors, fade out messages after 4s
    if (hasError) {
      setTimeout(() => {
        $('#name_msg, #email_msg, #phone_msg, #business_type_msg, #businessAddress, #country, #state, #city, #pincode, #providername, #musername, #mpassword')
          .fadeOut(500, function () { $(this).empty().show(); });
      }, 4000);
      return;
    }

    // Construct request body
    const requestBody = {
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
      username: fields.username,
      password: fields.password,
      createdBy: fields.createdBy, 
      productRoutes: productRoutes
    };

    // AJAX call
    $.ajax({
      url: '${url}'+'/app/public/onboardMerchant',
      type: 'POST',
      contentType: 'application/json',
      data: JSON.stringify(requestBody),
      beforeSend: function () {
    	  ajaxindicatorstart('⚙️ Processing merchant onboarding... Please wait.');
	    },
      success: function (response) {
    	  ajaxindicatorstop(); // ✅ Stop loader on success
    	  console.log("merchnat onboard data"+JSON.stringify(response))
        const msgColor = response.responseCode === 200 ? 'green' : 'red';
        $('#onboard_msg').empty().append(response.message).css({ color: msgColor, fontWeight: 'bold' });
        if (response.responseCode === 200) {
            var responseData = response.data;
            var mid = responseData.mid;
            var name = responseData.name;
            // Encode mid and name to base64url
            var encodedMid = encodeToBase64Url(mid);
            var encodedName = encodeToBase64Url(name);
            console.log("The API uses data (base64url-encoded): " + encodedMid);
            $('#merchant_frm').trigger('reset');
            setTimeout(() => {
                $('#onboard_msg').fadeOut(500, function() {
                    $(this).empty().show();
                    // Use encodedName here
                    window.location.href = '${url}'+'/app/admin/merchantRoutingView?mid=' + encodeURIComponent(encodedMid) + '&name=' + encodeURIComponent(encodedName);
                });
            }, 3000);
        }

      },
      error: function (xhr, status, error) {
    	  ajaxindicatorstop(); // ✅ Stop loader on error
        $('#onboard_msg').empty().append('Error during onboarding!').css({ color: 'red', fontWeight: 'bold' });
        setTimeout(() => $('#onboard_msg').fadeOut(500, function () { $(this).empty(); }), 4000);
        console.error('Error:', error);
        
      }
    });
  });

  function showMessage(selector, message) {
    $(selector).empty().append(message).css({ color: 'red', fontWeight: 'bold' });
  }
});




function encodeToBase64Url(input) {
	  const utf8Bytes = new TextEncoder().encode(input);
	  let base64String = btoa(String.fromCharCode(...utf8Bytes));
	  // Convert to base64url by replacing + and /, and removing padding =
	  const base64UrlString = base64String.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
	  return base64UrlString;
	}
</script>

<script>
const dropdown = document.getElementById('productsDropdown');
const list = document.getElementById('productsList');

dropdown.addEventListener('click', () => {
  list.style.display = (list.style.display === 'none' || list.style.display === '') ? 'block' : 'none';
});

// Close dropdown if clicked outside
document.addEventListener('click', function(event) {
  if (!dropdown.contains(event.target) && !list.contains(event.target)) {
    list.style.display = 'none';
  }
});
</script>
