<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    int currentYear = java.time.Year.now().getValue();
%>
</main>

<footer class="footer">
    <div class="container">
        <div class="footer-bottom">
            &copy; <%= currentYear%> NexaMart. All rights reserved.
        </div>
    </div>
</footer>

</body>
</html>