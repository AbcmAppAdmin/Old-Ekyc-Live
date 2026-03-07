<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> <%@ taglib
uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!doctype html>
<html>
    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale=1.0" />
        <link rel="preconnect" href="https://fonts.googleapis.com" />
        <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
        <link
            href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300..800;1,300..800&family=Poppins:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&family=Roboto+Condensed:ital,wght@  1  2  3  4  5  6  7  8  9   display=swap"
            rel="stylesheet"
        />
        <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css"
            integrity=""
            crossorigin="anonymous"
        />
        <!-- Bootstrap 5 CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" />

        <meta charset="UTF-8" />
        <title>eKYC | Invalid Request</title>
        <style>
            body {
                font-family: "Open Sans", sans-serif;
                font-optical-sizing: auto;
                background-color: #f4f6f9;
                margin: 0;
                padding: 0;
            }
            /* Header */

            /* Message box */
            .message-box-container {
                display: flex;
                justify-content: center;
                align-items: center;
                height: calc(100vh - 70px);
            }
            .message-box {
                background: #ffffff;
                padding: 40px 30px;
                border-radius: 10px;
                box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
                max-width: 500px;
                width: 90%;
                text-align: center;
            }
            .message-box p {
                color: #d92634;
                font-weight: 500;
                font-size: 13px;
                line-height: 0.51;
            }
            .btn {
                padding: 10px 25px;
                font-size: 16px;
                background: linear-gradient(90deg, #105894, #ec222b);
                color: #fff;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                margin-top: 20px;
                transition: background 0.3s ease;
            }
            .btn:hover {
                background-color: #0d47a1;
            }

            /* header css start */
            .header-section {
                background: linear-gradient(90deg, #105894, #ec222b);
                color: #fff;
                padding: 0.15rem;
            }
            .inner-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                max-width: 1200px;
                margin: 0 auto;
                padding: 1rem;
            }
            .inner-header .logo img {
                width: 120px;
                height: auto;
            }
            .email span i {
                font-size: 12px;
                color: #f0ffa6;
            }
            /* header css start end */

            /* media css start */

            @media only screen and (max-width: 600px) {
                .message-box {
                    padding: 30px 20px;
                    max-width: 90%;
                }

                .message-box p {
                    font-size: 12px;
                    line-height: 1.2;
                }

                .pay-imagee img {
                    width: 100%;
                    margin-bottom: -9px;
                }
                .inner-header {
                    flex-direction: column-reverse;
                }
                .header-section {
                    background: linear-gradient(90deg, #d7e8f7, #ff8d92);
                }
            }
            /* media css end */
        </style>
    </head>
    <body>
        <!-- Header with email on left, logo on right -->
        <!-- <div class="header bg-primary">
        <div class="email">noreply@abcmapp.com</div>
        <div class="logo">
            <img src="/assets/images/logo.png" alt="eKYC Logo">
        </div>
    </div> -->

        <section class="header-section">
            <div class="inner-header">
                <div class="email">
                    <span><i class="far fa-envelope-open"></i></span> noreply@abcmapp.com
                </div>
                <div class="logo">
                    <img src="https://ekyc.ablepay.in/view-kyc/assets/images/ablepay.webp" alt="ablepay" />
                </div>
            </div>
        </section>

        <!-- Message Box -->
        <div class="message-box-container">
            <div class="message-box">
                <c:choose>
                    <c:when test="${showRequestId}">
                        <p>${errorMessage}</p>
                        <p>Please check the Request ID and try again.</p>
                    </c:when>
                    <c:otherwise>
                        <p>${exceptionError}</p>
                        <p>Check the requested Request ID and try again.</p>
                    </c:otherwise>
                </c:choose>
                <button class="btn" onclick="window.close()">Close</button>
            </div>
        </div>
        <!-- Bootstrap 5 JS Bundle -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
