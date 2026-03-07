<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	<%@ include file="AdminHeader.jsp"%>
<head>

<title>Audit Trail</title>


<script
	src="https://cdn.jsdelivr.net/npm/jspdf@2.5.1/dist/jspdf.umd.min.js"></script>
<script
	src="https://cdn.jsdelivr.net/npm/jspdf-autotable@3.8.2/dist/jspdf.plugin.autotable.min.js"></script>

<style>
.audit-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	border-top: 1px solid #bfbfbf; /* ← THIS creates the line */
	padding-top: 12px;
}

.footer-ip {
	font-weight: bold;
	font-size: 13px;
}



/* A4-like card */
.audit-a4 {
	margin: 0 auto 30px auto;
	background: #fff;
	border: 1px solid #d9d9d9;
	padding: 26px 34px;
}

/* Header: logo left, title center */
.audit-topbar {
	display: flex;
	align-items: flex-start;
	justify-content: space-between;
	margin-bottom: 10px;
}

.audit-logo img {
	height: 26px;
	object-fit: contain;
}

.audit-title {
	flex: 1;
	text-align: center;
	font-weight: 700;
	letter-spacing: 1px;
	font-size: 18px;
	margin-top: 2px;
}

.audit-title-spacer {
	width: 140px; /* balances the logo width so title stays centered */
}

/* Key-value section like PDF: 2 columns + horizontal rule per row */
.kv-block {
	margin-top: 14px;
	border-top: 1px solid #bfbfbf;
}

.kv-row {
	display: grid;
	grid-template-columns: 180px 1fr;
	gap: 14px;
	padding: 10px 0;
	border-bottom: 1px solid #bfbfbf;
	font-size: 13px;
}

.kv-label {
	font-weight: 700;
}

.kv-value {
	word-break: break-word;
}

/* Stamp details in 2 columns (left label, right multi-line) */
.stamp-row .kv-value {
	display: grid;
	grid-template-columns: 160px 1fr;
	gap: 8px;
	row-gap: 4px;
}

.stamp-row .kv-value b {
	font-weight: 700;
}

/* About strip like PDF */
.about-strip {
	margin-top: 12px;
	background: #efefef;
	border: 1px solid #e0e0e0;
	padding: 10px 12px;
	display: grid;
	grid-template-columns: 170px 1fr;
	gap: 14px;
	font-size: 12px;
	line-height: 1.35;
}

.about-strip .about-title {
	font-weight: 700;
}

/* Table like PDF: NO vertical borders, only horizontal separators */
table.audit-table {
	width: 100%;
	border-collapse: collapse;
	margin-top: 14px;
	font-size: 12px;
}

table.audit-table thead th {
	text-align: left;
	font-weight: 700;
	padding: 10px 8px;
	border-bottom: 1px solid #bfbfbf;
}

table.audit-table tbody td {
	vertical-align: top;
	padding: 12px 8px;
	border-bottom: 1px solid #bfbfbf;
	line-height: 1.35;
}

.muted-dot {
	display: block;
	margin-left: 10px;
}

.download-btn {
	background: #4a3aff;
	color: #fff;
	padding: 8px 16px;
	border: none;
	cursor: pointer;
	margin-top: 16px;
	font-size: 13px;
}

#showReport {
	margin-top: 25px;
}

.download-btn:hover {
	background: #372dcf;
}

.audit-footer .download-btn {
	margin-top: 0 !important; /* IMPORTANT */
}
</style>
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
								<h2 class="mb-0">E-Sign Audit Report</h2>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row mb-3">
				<!-- Date Range with Label -->
				<div class="col-md-3 col-12">
					<label for="pc-date_range_picker-2" class="form-label">Select
						Date Range</label>
					<div class="input-group">
						<span class="input-group-text"><i
							class="feather icon-calendar"></i></span> <input type="text"
							id="pc-date_range_picker-2" class="form-control"
							placeholder="Select date range" name="dateRange" />
					</div>
					<!-- Show range here manually -->
					<small id="date-range-label"
						style="color: green; font-weight: bold;"></small>
				</div>

				<!-- MID Select with Label -->
				<div class="col-md-3 col-12">
					<label for="midSelect" class="form-label">Select MID</label> <select
						class="form-select form-select w-100" id="midSelect" name="mid">
						<option value="">All Merchants</option>
						<!-- All merchants option -->
						<c:forEach var="merchant" items="${assignMerchant}">

							<option value="${merchant.merchantId}">${merchant.merchantId}-${merchant.merchantName}</option>
						</c:forEach>
					</select>
				</div>

				<!-- Product Select with Label -->
				<div class="col-md-3 col-12">
					<button type="button" id="showReport"
						class="btn btn-shadow btn-info">
						<i class="fa fa-save me-2"></i> Submit
					</button>
				</div>


			</div>


			<div id="auditContainer"></div>
		</div>
	</div>

	<%@ include file="AdminFooter.jsp"%>
	<script>
    function escHtml(s) {
      if (s === null || s === undefined) return "-";
      return String(s)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll("\"", "&quot;");
    }

    function formatDateTime(iso) {
      if (!iso) return "-";
      try {
        const d = new Date(iso);
        if (isNaN(d.getTime())) return iso;
        return d.toLocaleString();
      } catch (e) {
        return iso;
      }
    }

    function createAuditHTML(doc) {
    	  const safe = (v) =>
    	    (v === null || v === undefined || String(v).trim() === "" ? "-" : String(v));

    	  var statusText =(doc.status && String(doc.status).trim());
		  if(statusText==='Complete')
		  {
			   statusText = "Executer";
		  }
			
			

    	  const docHash = safe(doc.documentHash);
    	  const stampState = safe(doc.state);
    	  const stampAmount = safe(doc.totalStampAmount || doc.stampAmount);
    	  const stampSerial = safe(doc.serialNumbers || doc.serialNumber || doc.stampSerial);

    	  let signerRows = "";
    	  let lastIp = "-";

    	  (doc.signers || []).forEach((s, idx) => {
    	    const name = safe(s.signerName);
    	    const email = safe(s.signerEmail);
    	    const role = safe(s.role);

    	    const dt = s.signedAt ? formatDateTime(s.signedAt) : "-";

    	    const ip = safe(s.ipAddress);
    	    lastIp = ip; // keep last signer ip (or you can keep first)
    	    const activity = safe(s.notification);
    	    const notificationText = (name !== "-") ? name : "-";

    	    signerRows +=
    	      "<tr>" +
    	        "<td>" + (idx + 1) + ".</td>" +
    	        "<td>" +
    	          "<b>Name:</b> " + escHtml(name) + "<br>" +
    	          "<b>Email:</b> " + escHtml(email) + "<br>" +
    	          "<b>Role:</b> " + escHtml(role) +
    	        "</td>" +
    	        "<td>" + escHtml(dt) + "</td>" +
    	        "<td>" +
    	          "<b>IP:</b> " + escHtml(ip) + "<br>" +
    	          "<b>Activity:</b> " + escHtml(activity) + "<br>" +
    	          "<b>Notification:</b> " + escHtml(notificationText) +
    	        "</td>" +
    	      "</tr>";
    	  });

    	  // footer ip: if you want FIRST signer IP, use doc.signers[0].ipAddress
    	  const footerIp = (doc.signers && doc.signers.length) ? safe(doc.signers[0].ipAddress) : lastIp;
		  
          var clientUrl='${clientUrl}'+'/assets/images/ablepay.webp';
    	  return (
    	    "<div class='audit-a4'>" +

    	      "<div class='audit-topbar'>" +
    	        "<div class='audit-logo'>" +
    	         "<img src='" +clientUrl+ "' alt='Logo' />" +
    	        "</div>" +
    	        "<div class='audit-title'>AUDIT TRAIL</div>" +
    	        "<div class='audit-title-spacer'></div>" +
    	      "</div>" +

    	      "<div class='kv-block'>" +
    	        "<div class='kv-row'><div class='kv-label'>Document Name</div><div class='kv-value'>" + escHtml(safe(doc.documentName)) + "</div></div>" +
    	        "<div class='kv-row'><div class='kv-label'>Document Order Id</div><div class='kv-value'>" + escHtml(safe(doc.documentId)) + "</div></div>" +
    	        "<div class='kv-row'><div class='kv-label'>Document Hash</div><div class='kv-value'>" + escHtml(safe(doc.documentHash)) + "</div></div>" +

    	        "<div class='kv-row'><div class='kv-label'>Status</div><div class='kv-value'>" + statusText + "</div></div>" +
    	      "</div>" +

    	      "<div class='about-strip'>" +
    	        "<div class='about-title'>About the Audit Trail</div>" +
    	        "<div>This is the Audit Trail of the signed document. It provides a detailed record of all the actions taken during the signing process.</div>" +
    	      "</div>" +

    	      "<table class='audit-table'>" +
    	        "<thead><tr><th style='width:70px;'>Sr. No.</th><th style='width:260px;'>Name &amp; Contact</th><th style='width:180px;'>Date</th><th>Details</th></tr></thead>" +
    	        "<tbody>" + (signerRows || "<tr><td colspan='4'>No signer data</td></tr>") + "</tbody>" +
    	      "</table>" +

    	      "<div class='audit-footer'>" +
    	        "<button class='download-btn' onclick=\"downloadPDF('" + escJs(doc.documentId) + "')\">Download PDF</button>" +
    	        "<div class='footer-ip'>IP: " + escHtml(footerIp) + "</div>" +
    	      "</div>" +

    	    "</div>"
    	  );
    	}
    // helper to safely use documentId inside onclick string
    function escJs(s) {
      if (s === null || s === undefined) return "";
      return String(s).replaceAll("\\", "\\\\").replaceAll("'", "\\'");
    }
    let auditResponse = null;
    function loadAudit() {

        const dateRange = document.getElementById("pc-date_range_picker-2").value;
        const mid = document.getElementById("midSelect").value;

        if (!dateRange) {
            alert("Please select date range");
            return;
        }

        let parts = dateRange.split("to");

        if (parts.length !== 2) {
            alert("Invalid date range format. Use: DD-MM-YYYY to DD-MM-YYYY");
            return;
        }

        let fromDate = parts[0].trim();
        let toDate = parts[1].trim();
            var esignUrl='${esignUrl}';
            const url = esignUrl + '/api/esign/signerAuditReport' +
            '?merchantId=' + (mid || '') +
            '&fromDate=' + fromDate +
            '&toDate=' + toDate;


        console.log("Calling API:", url);
        
        ajaxindicatorstart('📊 Fetching Audit Report...');

        fetch(url)
            .then(res => res.json())
            .then(response => {

                auditResponse = response; // 🔥 store globally for PDF

                let html = "";

                (response.data?.items || []).forEach(doc => {
                    html += createAuditHTML(doc);
                    ajaxindicatorstop();
                });

                if (!html) {
                    html = ""
                        + "<div style='"
                        + "width:100%;"
                        + "padding:20px 40px;"
                        + "'>"
                        + "<div style='"
                        + "background:#fff5f5;"
                        + "border:1px solid #f5c2c7;"
                        + "color:#dc3545;"
                        + "padding:15px;"
                        + "border-radius:6px;"
                        + "text-align:center;"
                        + "font-weight:600;"
                        + "'>"
                        + "No Records Found"
                        + "</div>"
                        + "</div>";

                    ajaxindicatorstop();
                }

                document.getElementById("auditContainer").innerHTML = html;
            })
            .catch(error => {
                console.error("API Error:", error);
                alert("Error fetching audit report");
                ajaxindicatorstop();
            });
        
    }


    loadAudit();

    
    
    function convertImageToBase64(imageUrl, callback) {
    	  const img = new Image();
    	  img.crossOrigin = "anonymous";  // ✅ important

    	  img.onload = function () {
    	    const canvas = document.createElement("canvas");
    	    canvas.width = img.width;
    	    canvas.height = img.height;

    	    const ctx = canvas.getContext("2d");
    	    ctx.drawImage(img, 0, 0);

    	    callback(canvas.toDataURL("image/png"));
    	  };

    	  img.onerror = function () {
    	    callback(null);
    	  };

    	  img.src = imageUrl;
    	}

 // ✅ Add this helper (keep your existing formatDateTime() as-is if you use it elsewhere)
    function formatDateTimeForPdf(iso) {
      if (!iso) return "-";
      try {
        const d = new Date(iso);
        if (isNaN(d.getTime())) return iso;

        const dd = String(d.getDate()).padStart(2, "0");
        const mm = String(d.getMonth() + 1).padStart(2, "0");
        const yy = d.getFullYear();
        const hh = String(d.getHours()).padStart(2, "0");
        const mi = String(d.getMinutes()).padStart(2, "0");
        const ss = String(d.getSeconds()).padStart(2, "0");

        // ✅ single-line format (won't wrap like "AM" on next line)
        return dd + "-" + mm + "-" + yy + " " + hh + ":" + mi + ":" + ss;
      } catch (e) {
        return iso;
      }
    }

    function downloadPDF(documentId) {
      if (!auditResponse || !auditResponse.data) {
        alert("No data available for PDF generation");
        return;
      }

      const docData = (auditResponse.data.items || []).find(d => d.documentId === documentId);
      if (!docData) {
        alert("Document not found: " + documentId);
        return;
      }

      const jsPDF = window.jspdf?.jsPDF;
      if (!jsPDF) {
        alert("jsPDF not loaded.");
        return;
      }

      const pdf = new jsPDF("p", "pt", "a4");
      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();

      const marginX = 60;
      const topY = 55;

      const safe = (v) =>
        v === null || v === undefined || String(v).trim() === "" ? "-" : String(v);

      // ✅ Logo URL
      const imageURL = '${clientUrl}' + '/assets/images/ablepay.webp';

      function generatePDF(logoBase64) {

        // ===== HEADER (LOGO + TITLE) =====
        if (logoBase64) {
          try {
            pdf.addImage(logoBase64, "PNG", marginX, topY - 35, 95, 22);
          } catch (e) {
            console.warn("Logo addImage failed:", e);
          }
        }

        // ✅ KEEP ONLY ONE TITLE
        pdf.setFont("helvetica", "bold");
        pdf.setFontSize(8);
        pdf.text("AUDIT TRAIL", pageWidth / 2, topY, { align: "center" });

        // ===== DOCUMENT DETAILS =====
        let y = 95;
        const labelX = marginX;
        const valueX = marginX + 125;
        const lineRight = pageWidth - marginX;

        pdf.setFontSize(8);
        pdf.setDrawColor(200);
        var docStatus = safe(docData.status);

if (docStatus === "Complete") {
    docStatus = "Executer";
}

const rows = [
  ["Document Name", safe(docData.documentName)],
  ["Document OrderId", safe(docData.documentId)],
  ["Document Hash", safe(docData.documentHash)],
  ["Status", docStatus]
];

        rows.forEach(r => {
          if (y > pageHeight - 140) {
            pdf.addPage();
            y = 70;
          }

          pdf.setFont("helvetica", "bold");
          pdf.text(r[0], labelX, y);

          pdf.setFont("helvetica", "normal");
          const maxW = lineRight - valueX;
          const wrappedVal = pdf.splitTextToSize(String(r[1]), maxW);
          pdf.text(wrappedVal, valueX, y);

          const rowHeight = (wrappedVal.length * 12);
          const lineY = y + rowHeight + 6;
          pdf.line(labelX, lineY, lineRight, lineY);

          y = lineY + 14;
        });

        // ===== ABOUT STRIP =====
        const stripX = marginX;
        const stripW = pageWidth - (marginX * 1.5);
        const leftColW = 140;
        const stripPad = 15;

        const aboutTitle = "About the Audit Trail";
        const aboutText =
          "This is the Audit Trail of the signed document. It provides a detailed record of all the actions taken during the signing process.";

        pdf.setFont("helvetica", "normal");
        pdf.setFontSize(8);

        const rightTextMaxW = stripW - leftColW - (stripPad * 2);
        const wrappedAbout = pdf.splitTextToSize(aboutText, rightTextMaxW);

        const lineH = 10;
        const stripH = Math.max(30, (wrappedAbout.length * lineH) + (stripPad * 2));

        if (y + stripH > pageHeight - 120) {
          pdf.addPage();
          y = 70;
        }

        pdf.setFillColor(239, 239, 239);
        pdf.setDrawColor(224, 224, 224);
        pdf.rect(stripX, y + 6, stripW, stripH, "FD");

        pdf.setFont("helvetica", "bold");
        pdf.setFontSize(10);
        pdf.text(aboutTitle, stripX + stripPad, y + 18);

        pdf.setFont("helvetica", "normal");
        pdf.setFontSize(9);
        pdf.text(wrappedAbout, stripX + leftColW, y + 18);

        y = y + stripH + 18;

        // ===== TABLE =====
        const signers = docData.signers || [];

        const bodyRows = signers.map((s, idx) => {
          const nameContact =
            "Name: " + safe(s.signerName) + "\n" +
            "Email: " + safe(s.signerEmail) + "\n" +
            "Role: " + safe(s.role);

          // ✅ FIX: use single-line date format for PDF (no AM/PM wrap)
          const date = s.signedAt ? formatDateTimeForPdf(s.signedAt) : "-";

          const details =
            "IP: " + safe(s.ipAddress) + "\n" +
            "Activity: " + safe(s.notification) + "\n" +
            "Notification: " + safe(s.signerName);

          return [
            String(idx + 1),
            nameContact,
            date,
            details
          ];
        });

        // ✅ FIX: proper table width so Date/IP don't shift or wrap badly
        const tableW = pageWidth - (marginX * 2);

        pdf.autoTable({
          startY: y,
          margin: { left: marginX, right: marginX },
          head: [["Sr. No.", "Name & Contact", "Date", "Details"]],
          body: bodyRows.length ? bodyRows : [["-", "No data", "-", "-"]],
          theme: "plain",
          styles: {
            font: "helvetica",
            fontSize: 7,
            cellPadding: 7,
            valign: "top"
          },
          headStyles: { fontStyle: "bold" },

          // ✅ UPDATED WIDTHS (Details gets more width so IP doesn't split)
          columnStyles: {
            0: { cellWidth: 45, halign: "center" },
            1: { cellWidth: 200 },
            2: { cellWidth: 110 },
            3: { cellWidth: tableW - 45 - 200 - 110 }
          },

          didDrawRow: function (data) {
            if (data.section === "body") {
              const doc = data.doc;
              doc.setDrawColor(210);
              doc.setLineWidth(0.8);
              doc.line(
                marginX,
                data.row.y + data.row.height,
                pageWidth - marginX,
                data.row.y + data.row.height
              );
            }
          }
        });

        // ===== FOOTER LINE + RIGHT IP =====
        let finalY = pdf.lastAutoTable.finalY + 15;

        if (finalY > pageHeight - 60) {
          pdf.addPage();
          finalY = 70;
        }

        pdf.line(marginX, finalY, pageWidth - marginX, finalY);

        pdf.setFont("helvetica");
        pdf.setFontSize(8);

        const footerIp = signers.length ? safe(signers[0].ipAddress) : "-";
        pdf.text("IP: " + footerIp, pageWidth - marginX, finalY + 18, { align: "right" });

        pdf.save(documentId + "_Audit.pdf");
      }

      // ✅ Convert image URL to base64, then generate PDF
      convertImageToBase64(imageURL, function (base64Img) {
        generatePDF(base64Img);
      });
    }
  </script>
	<script type="text/javascript">
  document.getElementById("showReport").addEventListener("click", function () {
	    loadAudit();
	});
  </script>

