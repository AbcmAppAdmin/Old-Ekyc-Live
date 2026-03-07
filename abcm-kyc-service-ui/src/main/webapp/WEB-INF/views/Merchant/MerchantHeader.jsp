<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<spring:eval
	expression="@environment.getProperty('ContextPath', 'https://default.url')"
	var="url" />
<!DOCTYPE html>
<html lang="en">
<!-- [Head] start -->

<head>
<!-- [Meta] -->
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<!-- <meta http-equiv="X-UA-Compatible" content="IE=edge" /> -->

<!-- [Favicon] icon -->
<link rel="icon" href="${url}/assets/images/favicon.png" type="image/x-icon" />
<!-- [Font] Family -->
<link rel="stylesheet" href="${url}/assets/fonts/inter/inter.css" id="main-font-link" />
<link rel="stylesheet" href="${url}/assets/fonts/phosphor/duotone/style.css" />
<link rel="stylesheet" href="${url}/assets/fonts/tabler-icons.min.css" />
<link rel="stylesheet" href="${url}/assets/fonts/feather.css" />
<link rel="stylesheet" href="${url}/assets/fonts/fontawesome.css" />
<link rel="stylesheet" href="${url}/assets/fonts/material.css" />
<link rel="stylesheet" href="${url}/assets/css/style.css" id="main-style-link" />
<link rel="stylesheet" href="${url}/assets/css/kyc-dashboard.css" />
<link rel="stylesheet" href="${url}/assets/css/merchant-dashboard.css" />
<link rel="stylesheet" href="${url}/assets/css/kyc-media.css" />
<!-- <script src="${url}/assets/js/tech-stack.js"></script> -->
<link rel="stylesheet" href="${url}/assets/css/style-preset.css" />
<!-- <link rel="stylesheet" href="assets/css/plugins/vanillatree.css" /> -->
<link rel="stylesheet" href="${url}/assets/css/plugins/flatpickr.min.css" />
<!-- <link rel="stylesheet" href="${url}/assets/css/plugins/vanillatree.css" /> -->
<link rel="stylesheet" href="${url}/assets/css/plugins/flatpickr.min.css" />
<!-- <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css"> -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

</head>
<!-- [Head] end -->
<!-- [Body] Start -->

<body data-pc-preset="preset-1" data-pc-sidebar-caption="true" data-pc-layout="vertical" data-pc-direction="ltr" data-pc-theme_contrast="" data-pc-theme="light">
	<!-- [ Pre-loader ] start -->
	<div class="loader-bg">
		<div class="loader-track">
			<div class="loader-fill"></div>
		</div>
	</div>
	<!-- [ Pre-loader ] End -->
	<!-- [ Sidebar Menu ] start -->
	<nav class="pc-sidebar" id="kycsidebar">
		<div class="navbar-wrapper">
			<div class="m-header kyc-logo">
				<a href="#" class="b-brand text-primary"> <!-- ========   Change your logo from here   ============ -->
					<img src="${url}/assets/images/ablepay.webp" class="img-fluid" alt="logo" />
				</a>
			</div>

			<div class="navbar-content">
				<ul class="pc-navbar">
					<li class="pc-item pc-caption"><label data-i18n="Navigation">Navigation</label> </li>


					<c:if test="${merchant.dashboard == 'ENABLE'}">
						<li class="pc-item pc-hasmenu"><a
							href="${url}/app/merchant/dashboard" class="pc-link"> <span
								class="pc-micon"> <svg class="pc-icon">
                                        <use xlink:href="#custom-element-plus"></use>
                                    </svg>
							</span> <span class="pc-mtext" data-i18n="Dashboard">Dashboard</span>
						</a></li>
					</c:if>

					<c:if test="${merchant.product == 'ENABLE'}">
						<li class="pc-item"><a href="${url}/app/merchant/products"
							class="pc-link"> <span class="pc-micon"> <svg
										class="pc-icon">
                    <use xlink:href="#custom-layer"></use>
                </svg>
							</span> <span class="pc-mtext" data-i18n="Products">Products</span>
						</a></li>
					</c:if>


					<c:if test="${merchant.billing == 'ENABLE'}">
						<li class="pc-item"><a href="${url}/app/merchant/billing"
							class="pc-link"> <span class="pc-micon"> <svg
										class="pc-icon">
                                        <use
											xlink:href="#custom-note-1"></use>
                                    </svg>
							</span> <span class="pc-mtext" data-i18n="Billing">Billing</span>
						</a></li>
					</c:if>



					<%-- <c:if test="${merchant.apps == 'ENABLE'}">
						<li class="pc-item"><a href="${url}/app/merchant/Apps"
							class="pc-link"> <span class="pc-micon"> <svg
										class="pc-icon">
                                        <use
											xlink:href="#custom-clipboard"></use>
                                    </svg>
							</span> <span class="pc-mtext" data-i18n="Apps">Apps</span>
						</a></li>
					</c:if> --%>
					<c:if test="${merchant.kycReport == 'ENABLE'}">
						<li class="pc-item"><a href="${url}/app/merchant/kycReport"
							class="pc-link"> <span class="pc-micon"> <svg
										class="pc-icon">
                                        <use
											xlink:href="#custom-calendar-1"></use>
                                    </svg>
							</span> <span class="pc-mtext" data-i18n="Transaction Report">Transaction
									Report</span>
						</a></li>
					</c:if>

					<li class="pc-item"><a
						href="${url}/app/merchant/txnCountReport" class="pc-link"> <span
							class="pc-micon"> <svg class="pc-icon">
                                        <use
										xlink:href="#custom-note-1"></use>
                                    </svg>
						</span> <span class="pc-mtext" data-i18n="Transaction Count">Transaction
								Count</span>
					</a></li>
					
					
					<c:if test="${merchant.esign == 'ENABLE'}">
					<li class="pc-item pc-hasmenu">
						<a href="#!" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon"> <use xlink:href="#custom-status-up"></use> </svg> 
							</span>
							<span class="pc-mtext" data-i18n="E-Sign">E-Sign</span>
							<span class="pc-arrow"><i data-feather="chevron-right"></i></span>
						</a>
						<ul class="pc-submenu">
							<li class="pc-item"><a class="pc-link" href="${url}/app/merchant/signer" data-i18n="E-Sign">E-Sign</a></li>
							<li class="pc-item"><a class="pc-link" href="${url}/app/merchant/esignReport" data-i18n="E-Sign Report">E-Sign Report</a></li>
							
						</ul>
					</li>
					</c:if>
				</ul>
			</div>
		</div>
	</nav>
	<!-- [ Sidebar Menu ] end -->
	<!-- [ Header Topbar ] start -->
	<header class="pc-header">
		<div class="header-wrapper">
			<!-- [Mobile Media Block] start -->
			<div class="me-auto pc-mob-drp">
				<ul class="list-unstyled">
					<!-- ======= Menu collapse Icon ===== -->
					<li class="pc-h-item pc-sidebar-collapse"><a href="#"
						class="pc-head-link ms-0" id="sidebar-hide"> <i
							class="ti ti-menu-2"></i>
					</a></li>
					<li class="pc-h-item pc-sidebar-popup"><a href="#"
						class="pc-head-link ms-0" id="mobile-collapse"> <i
							class="ti ti-menu-2"></i>
					</a></li>
					<li class="pc-h-item d-none d-md-inline-flex"></li>
				</ul>
			</div>

			<!-- [Mobile Media Block end] -->
			<div class="ms-auto">
				<ul class="list-unstyled">
					<li class="pc-h-item ">
						<a class="xx-name"> 
							${merchantName}
						</a>
					</li>
					<li class="dropdown pc-h-item header-user-profile"><a
						class="pc-head-link dropdown-toggle arrow-none me-0"
						data-bs-toggle="dropdown" href="#" role="button"
						aria-haspopup="false" data-bs-auto-close="outside"
						aria-expanded="false"> <img
							src="${url}/assets/images/user/avatar-2.jpg" alt="user-image" class="user-avtar" />
					</a>
						<div
							class="dropdown-menu dropdown-user-profile dropdown-menu-end pc-h-dropdown">
							<div
								class="dropdown-header d-flex align-items-center justify-content-between">
								<h5 class="m-0">Profile</h5>
							</div>
							<div class="dropdown-body">
								<div class="profile-notification-scroll position-relative"
									style="max-height: calc(100vh - 225px);">


									<a href="#" class="dropdown-item"> <span> <svg
												class="pc-icon text-muted me-2">
                                                    <use
													xlink:href="#custom-share-bold"></use>
                                                </svg> <span>${merchant.credentials.username}</span>
									</span>
									</a> <a href="#" class="dropdown-item" id="openmodel"> <span>
											<svg class="pc-icon text-muted me-2">
										            <use xlink:href="#custom-lock-outline"></use>
										        </svg> <span>Change Password</span>
									</span>
									</a> <a href="${url}/logout" class="dropdown-item"> <span>
											<svg class="pc-icon text-muted me-2">
            <use xlink:href="#custom-lock-outline"></use>
        </svg> <span>LogOut</span>
									</span>
									</a>
								</div>
							</div>
						</div></li>
				</ul>
			</div>
		</div>



	</header>


	<!-- Modal for changing password -->



		
	<div class="modal fade" id="changePasswordModal" tabindex="-1"
		aria-labelledby="changePasswordModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="background: #08508d;">
					<h5 class="modal-title text-white" id="changePasswordModalLabel"
						style="color: #ffffff;">Merchnat Update Password</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div id="passwordUpdateMessageContainer" class="text-center mb-2">
						<span id="passwordUpdateMessage"></span>
					</div>
					<form id="changePasswordMerchantForm">
						<div class="mb-3">
							<label for="currentPassword" class="form-label">Merchant
								Id</label> <input type="text" value="${merchant.mid}"
								class="form-control" id="mid" readonly="readonly">
						</div>
						<div class="mb-3 position-relative">
							<label for="newPassword" class="form-label">New Password</label>
							<input type="password" class="form-control pe-5" id="newPassword"
								placeholder="Enter a new password" required> <i
								class="bi bi-eye position-absolute end-0 top-50 translate-middle-y me-3 cursor-pointer"
								onclick="togglePassword('newPassword', this)"></i>
						</div>

						<div class="mb-3 position-relative">

							<label for="confirmPassword" class="form-label">Confirm
								New Password</label> <input type="password" class="form-control pe-5"
								id="confirmPassword" placeholder="Confirm your new password"
								required> <i
								class="bi bi-eye position-absolute end-0 top-50 translate-middle-y me-3 cursor-pointer"
								onclick="togglePassword('confirmPassword', this)"></i>
						</div>


					</form>
				</div>
				<div class="modal-footer flex-column">

					<div>
						<button type="button" class="btn btn-secondary"
							data-bs-dismiss="modal" onclick="clearPasswordForm()">Cancel</button>
						<button type="button" class="btn btn-primary" id="savePasswordBtn"
							onclick="changePassword()">Save Changes</button>
					</div>
				</div>
			</div>
		</div>
	</div>




	<!-- [ Header ] end -->

	<script type="text/javascript">
	document.getElementById("savePasswordBtn").addEventListener("click", function () {
		const requestBody = {
        	    mid: $('#mid').val(),
        	    newPassword: $('#newPassword').val(),
        	    confirmNewPassword: $('#confirmPassword').val()
        	  };
        	  fetch('${url}' + '/app/public/change-password', {
        	    method: "POST",
        	    headers: {
        	      "Content-Type": "application/json"
        	    },
        	    body: JSON.stringify(requestBody)
        	  })
        	  .then(response => response.json())
        	  .then(data => {
        	    const messageSpan = $("#passwordUpdateMessage");

        	    if (data.responseCode === 200) {
        	      // ✅ Show green bold text for success
        	      messageSpan
        	        .removeClass('text-danger fw-bold')
        	        .addClass('text-success fw-bold')
        	        .empty()
        	        .text(data.message || 'Password updated successfully!')
        	        .fadeIn()
        	        .delay(4000)
        	        .fadeOut(() => {
						clearPasswordForm();
        	            $('#changePasswordModal').modal('hide');
        	            
        	          });
        	    } else {
        	      // 🔴 Show red bold text for error
        	      messageSpan
        	        .removeClass('text-success fw-bold')
        	        .addClass('text-danger fw-bold')
        	        .empty()
        	        .text(data.message || 'Error changing password.')
        	        .fadeIn()
        	        .delay(4000)
        	        .fadeOut();
        	    }
        	  })
        	  .catch(error => {
        	    console.error("Error:", error);

        	    $("#passwordUpdateMessage")
        	      .removeClass('text-success fw-bold')
        	      .addClass('text-danger fw-bold')
        	      .empty()
        	      .text('An error occurred while changing password.')
        	      .fadeIn()
        	      .delay(4000)
        	      .fadeOut();
        	  });
	});
	
	</script>

	<script>
function togglePassword(fieldId, btn) {
  const input = document.getElementById(fieldId);
  const icon = btn.querySelector('i');

  if (input.type === "password") {
    input.type = "text";
    icon.classList.remove("bi-eye");
    icon.classList.add("bi-eye-slash");
  } else {
    input.type = "password";
    icon.classList.remove("bi-eye-slash");
    icon.classList.add("bi-eye");
  }
}



document.getElementById("openmodel").addEventListener("click", function (e) {
	  e.preventDefault();

	  // Initialize and show the modal with the desired options
	  var modalElement = document.getElementById("changePasswordModal");
	  var modalInstance = new bootstrap.Modal(modalElement, {
	    backdrop: "static",
	    keyboard: false
	  });
	  modalInstance.show();
	});

	
	
	
	function clearPasswordForm() {
		    document.getElementById('changePasswordMerchantForm').reset();
		    document.getElementById('passwordUpdateMessage').textContent = '';
		}
</script>