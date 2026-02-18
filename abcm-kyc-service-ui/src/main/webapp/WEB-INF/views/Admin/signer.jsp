<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<head>
<%@ include file="AdminHeader.jsp"%>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>eSign Document Upload</title>

<style>
.form-group {
    position: relative;
    margin-bottom: 18px;
}

.form-group input {
    width: 100%;
    padding: 10px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.form-group label {
    position: absolute;
    left: 10px;
    top: 10px;
    background: #fff;
    padding: 0 4px;
    font-size: 14px;
    color: #777;
    transition: 0.2s;
    pointer-events: none;
}

.form-group input:focus + label,
.form-group input:not(:placeholder-shown) + label {
    top: -7px;
    font-size: 12px;
    color: #08508d;
}

.signer-box {
    background: #f8f9fa;
    padding: 15px;
    border: 1px solid #e5e5e5;
    border-radius: 5px;
    margin-bottom: 15px;
}
</style>
</head>

<div class="pc-container mt-4">
	<div class="pc-content">
		<div class="card mt-3">
			<div class="card-header p-2 rounded-0" style="background: #08508d;">
				<h5 class="mb-0 text-white">eSign Document Upload</h5>
			</div>

			<div class="card-body">

				<form id="esignForm" enctype="multipart/form-data">

					<div class="row">
						<div class="col-md-4 col-12">
							<div class="form-group">
								<input type="text" name="document_name" placeholder=" " required>
								<label>Document Name</label>
							</div>
						</div>

						<div class="col-md-4 col-12">
							<div class="form-group">
								<input type="file" name="file" required>
								<label>Upload PDF File</label>
							</div>
						</div>

						<div class="col-md-4 col-12">
							<div class="form-group">
								<input type="number" name="link_expiry_min" placeholder=" " required>
								<label>Link Expiry (Minutes)</label>
							</div>
						</div>
					</div>

					<hr>

					<h6 class="mb-3">Signer List</h6>
					<div id="signerContainer"></div>

					<div class="text-center mt-3">
						<button type="button" class="btn btn-success rounded-0"
							onclick="addSigner()">+ Add Signer</button>

						<button type="submit"
							class="btn btn-primary rounded-0 ms-2">Submit</button>
					</div>

				</form>

			</div>
		</div>
	</div>
</div>

<%@ include file="AdminFooter.jsp"%>

<script>
let signerCount = 0;

function addSigner() {

    if (signerCount >= 4) {
        alert("Maximum 4 signers allowed.");
        return;
    }

    signerCount++;

    let html = `
        <div class="signer-box">

            <div class="row">
                <div class="col-md-6 col-12">
                    <div class="form-group">
                        <input type="text" name="signer_name_${signerCount}" placeholder=" " required>
                        <label>Signer Name</label>
                    </div>
                </div>

                <div class="col-md-6 col-12">
                    <div class="form-group">
                        <input type="email" name="signer_email_${signerCount}" placeholder=" " required>
                        <label>Signer Email</label>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-6 col-12">
                    <div class="form-group">
                        <input type="text" name="purpose_${signerCount}" placeholder=" " required>
                        <label>Purpose</label>
                    </div>
                </div>

                <div class="col-md-6 col-12">
                    <div class="form-group">
                        <input type="number" name="page_num_${signerCount}" placeholder=" " value="1" min="1" required>
                        <label>Page Number</label>
                    </div>
                </div>
            </div>

            <div class="form-check mt-2">
                <input class="form-check-input" type="checkbox" name="email_notify_${signerCount}">
                <label class="form-check-label">Send Email Notification</label>
            </div>

            <div class="text-end mt-2">
                <button type="button" class="btn btn-danger btn-sm rounded-0"
                    onclick="this.closest('.signer-box').remove()">Remove</button>
            </div>

        </div>
    `;

    $("#signerContainer").append(html);
}
</script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<script>
$("#esignForm").on("submit", function(e) {
    e.preventDefault(); // Prevent default form submission

    // Create FormData object
    let formData = new FormData();

    // Basic document info
    //formData.append("merchant_id", "K10001"); // replace as per your logic
    formData.append("consent", "Y");
    formData.append("document_name", $("input[name=document_name]").val());
    formData.append("link_expiry_min", $("input[name=link_expiry_min]").val());
   // formData.append("order_id", "ABELPAY0015"); // generate dynamically if needed
    formData.append("webhook_url", "https://google.com");

    // Add file
    let fileInput = $("input[name=file]")[0];
    if(fileInput.files.length === 0){
        alert("Please select a file");
        return;
    }
    formData.append("file", fileInput.files[0]);

    // Prepare signer list
    let signers = [];
    $(".signer-box").each(function(){

        let signer_name = $(this).find("input[name^='signer_name']").val();
        let signer_email = $(this).find("input[name^='signer_email']").val();
        let signer_purpose = $(this).find("input[name^='purpose']").val();
        let page_num = $(this).find("input[name^='page_num']").val();
        let isChecked = $(this).find("input[type='checkbox']").is(":checked");
        let signerObj = {
            signer_name: signer_name,
            signer_email: signer_email,
            signer_purpose: signer_purpose,
            sign_coordinates: [{ page_num: parseInt(page_num) }]
        };
        if(isChecked){
            signerObj.email_notification = "SEND";
        }
        signers.push(signerObj);
    });
    formData.append("signers", JSON.stringify(signers));
    $.ajax({
        url: "http://localhost:8087/app/public/submit/esign",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        beforeSend: function () {
        	ajaxindicatorstart('⚙️ Processing AaDhaar Esign. Please wait.');
        },
        success: function(response){
            console.log("API Response:", response);
            if(response && response.responseCode === 200){
                
                if(response.data && response.data.response_code === "200"){
                    alert(response.data.response_message);
                } else {
                    alert(response.data ? response.data.response_message : "Esign Failed");
                }
            } else {
                alert(response.message || "Esign Failed");
            }
            ajaxindicatorstop();
        },
        error: function(err){
            console.error(err);
            alert("Error submitting document. Check console.");
        }
    });
});
</script>

