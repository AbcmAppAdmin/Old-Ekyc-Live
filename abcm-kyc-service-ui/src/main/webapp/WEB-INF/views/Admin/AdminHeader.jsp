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
<title></title>
<!-- [Meta] -->
<meta charset="utf-8" />
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, user-scalable=0, minimal-ui" />
<meta http-equiv="X-UA-Compatible" content="IE=edge" />

<!-- [Favicon] icon -->
<link rel="icon" href="${url}/assets/images/favicon.png"
	type="image/x-icon" />
<!-- [Font] Family -->
<link rel="stylesheet" href="${url}/assets/fonts/inter/inter.css"
	id="main-font-link" />
<!-- [phosphor Icons] https://phosphoricons.com/ -->
<link rel="stylesheet"
	href="${url}/assets/fonts/phosphor/duotone/style.css" />
<!-- [Tabler Icons] https://tablericons.com -->
<link rel="stylesheet" href="${url}/assets/fonts/tabler-icons.min.css" />
<!-- [Feather Icons] https://feathericons.com -->
<link rel="stylesheet" href="${url}/assets/fonts/feather.css" />
<!-- [Font Awesome Icons] https://fontawesome.com/icons -->
<link rel="stylesheet" href="${url}/assets/fonts/fontawesome.css" />
<!-- [Material Icons] https://fonts.google.com/icons -->
<link rel="stylesheet" href="${url}/assets/fonts/material.css" />
<!-- [Template CSS Files] -->
<link rel="stylesheet" href="${url}/assets/css/style.css"
	id="main-style-link" />
<link rel="stylesheet" href="${url}/assets/css/kyc-dashboard.css" />
<link rel="stylesheet" href="${url}/assets/css/kyc-media.css" />
<script src="${url}/assets/js/tech-stack.js"></script>
<link rel="stylesheet" href="${url}/assets/css/style-preset.css" />
<link rel="stylesheet" href="assets/css/plugins/vanillatree.css" />
<link rel="stylesheet"
	href="${url}/assets/css/plugins/flatpickr.min.css" />
<link rel="stylesheet" href="${url}/assets/css/plugins/vanillatree.css" />
<link rel="stylesheet"
	href="${url}/assets/css/plugins/flatpickr.min.css" />
<link rel="stylesheet"
	href="${url}/assets/css/plugins/flatpickr.min.css" />

<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css">



</head>
<!-- [Head] end -->
<!-- [Body] Start -->

<body data-pc-preset="preset-1" data-pc-sidebar-caption="true"
	data-pc-layout="vertical" data-pc-direction="ltr"
	data-pc-theme_contrast="" data-pc-theme="light">
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
					<img src="${url}/assets/images/logo.png" class="img-fluid"
					alt="logo" /> <!-- <span class="badge bg-light-success rounded-pill ms-2 theme-version">v9.5.0</span> -->
				</a>
			</div>
			<div class="navbar-content">
				<ul class="pc-navbar">
					<li class="pc-item pc-caption"><label data-i18n="Navigation">Navigation</label> </li>
					
					<li class="pc-item pc-hasmenu"><a href="${url}/app/admin/dashboard" class="pc-link"> 
						<span class="pc-micon"> <svg class="pc-icon"> <use xlink:href="#custom-home"></use> </svg>
						</span> <span class="pc-mtext" data-i18n="Dashboard">Dashboard</span> </a>
					</li>
					
					<li class="pc-item"><a href="${url}/app/admin/products"
						class="pc-link"> <span class="pc-micon"> <svg
									class="pc-icon">
                                        <use
										xlink:href="#custom-add-item"></use>
                                    </svg>
						</span> <span class="pc-mtext" data-i18n="Products">Products</span>
					</a>
					</li>


					<li class="pc-item">
						<a href="${url}/app/admin/addMerchantBalance" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon">
									<use xlink:href="#custom-notification-status"></use>
								</svg>
							</span> 
							<span class="pc-mtext" data-i18n="Add Balance">Add Balance</span>
						</a>
					</li>
					
					
					<li class="pc-item pc-hasmenu">
						<a href="#!" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon"> <use xlink:href="#custom-status-up"></use> </svg> 
							</span>
							<span class="pc-mtext" data-i18n="Merchant Master">Merchant Master</span>
							<span class="pc-arrow"><i data-feather="chevron-right"></i></span>
						</a>
						<ul class="pc-submenu">
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/merchantOnboard" data-i18n="Merchant
								Onboarding">Merchant
								Onboarding</a></li>
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/clientOnboard" data-i18n="Provider
								Onboarding">Provider
								Onboarding</a></li>
						</ul>
					</li>
					
					
					<li class="pc-item pc-hasmenu">
						<a href="#!" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon"> <use xlink:href="#custom-status-up"></use> </svg> 
							</span>
							<span class="pc-mtext" data-i18n="Report">Report</span>
							<span class="pc-arrow"><i data-feather="chevron-right"></i></span>
						</a>
						<ul class="pc-submenu">
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/allReport" data-i18n="Onboarding
								Report">Onboarding
								Report</a></li>
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/kycReport" data-i18n="Kyc Report">Kyc Report</a></li>
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/billing" data-i18n="Recharge History">Recharge History
								</a></li>
								
								<li class="pc-item"><a class="pc-link" href="${url}/app/admin/kycCountReport" data-i18n="Transaction Count">Transaction Count</a></li>
								<li class="pc-item"><a class="pc-link" href="${url}/app/admin/Authntication" data-i18n="Authentication">Authentication</a></li>
						</ul>
					</li>
					
					<li class="pc-item pc-hasmenu">
						<a href="#!" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon"> <use xlink:href="#custom-status-up"></use> </svg> 
							</span>
							<span class="pc-mtext" data-i18n="Merchant Routing">Merchant Routing</span>
							<span class="pc-arrow"><i data-feather="chevron-right"></i></span>
						</a>
						<ul class="pc-submenu">
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/merchantRoutingViewUpdate" data-i18n="Route Config">Route Config</a></li>
							
						</ul>
					</li>
					
					<li class="pc-item pc-hasmenu">
						<a href="#!" class="pc-link"> 
							<span class="pc-micon"> 
								<svg class="pc-icon"> <use xlink:href="#custom-status-up"></use> </svg> 
							</span>
							<span class="pc-mtext" data-i18n="E-Sign">E-Sign</span>
							<span class="pc-arrow"><i data-feather="chevron-right"></i></span>
						</a>
						<ul class="pc-submenu">
							<li class="pc-item"><a class="pc-link" href="${url}/app/admin/signer" data-i18n="E-Sign">E-Sign</a></li>
							
						</ul>
					</li>
					
					
					<%-- <li class="pc-item pc-hasmenu"><a href="${url}/app/admin/ekycDemo" class="pc-link"> 
											<span class="pc-micon"> <svg class="pc-icon"> <use xlink:href="#custom-home"></use> </svg>
											</span> <span class="pc-mtext" data-i18n="Dashboard">ekyDemo</span> </a>
										</li>
 --%>
					


					



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
					<li class="pc-h-item d-none d-md-inline-flex">
						<!-- <form class="form-search">
                                <i class="search-icon">
                                    <svg class="pc-icon">
                                        <use xlink:href="#custom-search-normal-1"></use>
                                    </svg>
                                </i>
                                <input type="search" class="form-control" placeholder="Ctrl + K" />
                            </form> -->
					</li>
				</ul>
			</div>

			<!-- [Mobile Media Block end] -->
			<div class="ms-auto">
				<ul class="list-unstyled">
					<li class="dropdown pc-h-item header-user-profile"><a
						class="pc-head-link dropdown-toggle arrow-none me-0"
						data-bs-toggle="dropdown" href="#" role="button"
						aria-haspopup="false" data-bs-auto-close="outside"
						aria-expanded="false"> <img
							src="${url}/assets/images/user/avatar-2.jpg" alt="user-image"
							class="user-avtar" />
					</a>
						<div
							class="dropdown-menu dropdown-user-profile dropdown-menu-end pc-h-dropdown">

							<div class="dropdown-body">
								<div class="profile-notification-scroll position-relative"
									style="max-height: calc(100vh - 225px);">



									<a href="#" class="dropdown-item" id="openUsermodel"> <span> <svg
												class="pc-icon text-muted me-2">
                                                    <use
													xlink:href="#custom-lock-outline"></use>
                                                </svg> <span>Change
												Password</span>
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
	
	
	<div class="modal fade" id="userModel" tabindex="-1"
		aria-labelledby="changePasswordModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" style="background: #08508d;">
					<h5 class="modal-title text-white" id="changePasswordModalLabel"
						style="color: #ffffff;">User Update Password</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal"
						aria-label="Close"></button>
				</div>
				<div class="modal-body">
					<div id="passwordUpdateMessageContainer" class="text-center mb-2">
						<span id="passwordUpdateMessage"></span>
					</div>
					<form id="changePasswordForm">
						<div class="mb-3">
							<label for="currentPassword" class="form-label">UserName
								</label> <input type="text"
								class="form-control" id="username" placeholder="Enter your username">
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
	<script type="text/javascript">
	
	document.getElementById("openUsermodel").addEventListener("click", function (e) {
		  e.preventDefault();
		  // Initialize and show the modal with the desired options
		  var modalElement = document.getElementById("userModel");
		  var modalInstance = new bootstrap.Modal(modalElement, {
		    backdrop: "static",
		    keyboard: false
		  });
		  modalInstance.show();
		});
	
	</script>
	
	<script type="text/javascript">
	document.getElementById("savePasswordBtn").addEventListener("click", function () {
		const requestBody = {
        	    username: $('#username').val(),
        	    newPassword: $('#newPassword').val(),
        	    confirmNewPassword: $('#confirmPassword').val()
        	  };
        	  fetch('${url}' + '/app/public/user-change-password', {
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
        	        	//$('#changePasswordForm')[0].reset();
						clearPasswordForm();
        	            $('#userModel').modal('hide');
        	            
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
	
	
	
	function clearPasswordForm() {
	    document.getElementById('changePasswordForm').reset();
	    document.getElementById('passwordUpdateMessage').textContent = '';
	}

	</script>
	

	<!-- [ Header ] end -->