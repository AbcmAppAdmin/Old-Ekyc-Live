<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>eKYC | Invalid Request</title>
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f6f9;
            margin: 0;
            padding: 0;
        }
        /* Header */
        .header {
            width: 100%;
            background-color: #3560bf; /* Darker primary blue */
            color: #ffffff;
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 30px;
            box-sizing: border-box;
            box-shadow: 0 2px 6px rgba(0,0,0,0.2);
            font-weight: 500;
        }
        .header .email {
            font-size: 16px;
        }
        .header .logo img {
            height: 40px; 
        }

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
            box-shadow: 0 6px 20px rgba(0,0,0,0.2);
            max-width: 500px;
            width: 90%;
            text-align: center;
        }
        .message-box p {
            color: #d32f2f; /* red color for errors */
            font-weight: bold;
            margin: 15px 0;
            font-size: 16px;
        }
        .btn {
            padding: 10px 25px;
            font-size: 16px;
            color: #ffffff;
            background-color: #1976d2;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            margin-top: 20px;
            transition: background 0.3s ease;
        }
        .btn:hover {
            background-color: #0d47a1;
        }
    </style>
</head>
<body>
    <!-- Header with email on left, logo on right -->
    <div class="header">
        <div class="email">noreply@abcmapp.com</div>
        <div class="logo">
            <img src="/assets/images/logo.png" alt="eKYC Logo">
        </div>
    </div>

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
</body>
</html>
