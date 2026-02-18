<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>eKYC | Payment Status</title>
<style>
    body {
        font-family: Arial, sans-serif;
        background-color: #f4f6f9;
        text-align: center;
        margin: 0;
        padding: 0;
    }

    .message-box {
        display: inline-block;
        background-color: #fff;
        padding: 30px 20px;
        margin: 50px auto;
        border-radius: 10px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        max-width: 500px;
        width: 90%;
    }

    .message-box p {
        font-weight: bold;
        font-size: 16px;
        margin: 15px 0;
    }

    .success-text {
        color: green;
    }

    .error-text {
        color: red;
    }

    .pay-imagee img {
        width: 359px;
        margin-bottom: -9px;
    }

    .btn {
        padding: 10px 25px;
        font-size: 16px;
        color: #fff;
        background-color: #1976d2;
        border: none;
        border-radius: 5px;
        cursor: pointer;
        margin-top: 20px;
    }

    .btn:hover {
        background-color: #0d47a1;
    }
</style>
<script>
    function closedwindow() {
        window.close();
    }
</script>
</head>
<body>

<div class="message-box">
    <c:choose>
        <c:when test="${status == 'success'}">
            <div class="pay-imagee">
                <img src="/assets/images/sign-success.png" alt="Success">
            </div>
            <p class="success-text">
                Thank you! You have successfully signed the terms and conditions using Aadhaar eSign.
            </p>
        </c:when>
        <c:otherwise>
            <p class="error-text">Something went wrong. Please try again later.</p>
            <p class="error-text">Check the Request ID and try again.</p>
        </c:otherwise>
    </c:choose>

    <button class="btn" onclick="closedwindow();">Close</button>
</div>

</body>
</html>
