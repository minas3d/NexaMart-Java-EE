
(function () {
    "use strict";

    document.addEventListener("DOMContentLoaded", function () {

        var passwordInput = document.getElementById("password");
        var strengthBox   = document.getElementById("passwordStrength");
        var strengthLabel = document.getElementById("passwordStrengthLabel");

        if (passwordInput && strengthBox && strengthLabel) {
            passwordInput.addEventListener("input", function () {
                var value = passwordInput.value;

                if (value.length === 0) {
                    strengthBox.classList.remove("visible", "weak", "medium", "strong");
                    strengthLabel.textContent = "";
                    return;
                }

                strengthBox.classList.add("visible");

                var level = evaluateStrength(value);

                strengthBox.classList.remove("weak", "medium", "strong");
                strengthBox.classList.add(level);

                if (level === "strong") {
                    strengthLabel.textContent = "Strong";
                } else if (level === "medium") {
                    strengthLabel.textContent = "Medium";
                } else {
                    strengthLabel.textContent = "Weak";
                }
            });
        }

        var confirmInput = document.getElementById("confirmPassword");
        var matchMsg     = document.getElementById("passwordMatchMsg");

        if (passwordInput && confirmInput && matchMsg) {
            function checkMatch() {
                var pw  = passwordInput.value;
                var cpw = confirmInput.value;

                if (pw.length === 0 || cpw.length === 0) {
                    matchMsg.textContent = "";
                    matchMsg.classList.remove("match-ok", "match-fail");
                    return;
                }

                if (pw === cpw) {
                    matchMsg.textContent = "Passwords match";
                    matchMsg.classList.remove("match-fail");
                    matchMsg.classList.add("match-ok");
                } else {
                    matchMsg.textContent = "Passwords do not match";
                    matchMsg.classList.remove("match-ok");
                    matchMsg.classList.add("match-fail");
                }
            }

            passwordInput.addEventListener("input", checkMatch);
            confirmInput.addEventListener("input", checkMatch);
        }

        var picker = document.getElementById("countryPicker");
        if (picker) {
            var visibleInput = document.getElementById("countryCodeInput");
            var hiddenInput  = document.getElementById("countryCode");
            var flagImg      = document.querySelector("#countryFlag img");
            var list         = document.getElementById("countryList");
            var ctx          = picker.dataset.ctx || "";

            var COUNTRIES = [
                { code: "+20",  iso: "eg", name: "Egypt" },
                { code: "+1",   iso: "us", name: "United States" },
                { code: "+44",  iso: "gb", name: "United Kingdom" },
                { code: "+966", iso: "sa", name: "Saudi Arabia" },
                { code: "+971", iso: "ae", name: "United Arab Emirates" },
                { code: "+49",  iso: "de", name: "Germany" },
                { code: "+33",  iso: "fr", name: "France" },
                { code: "+91",  iso: "in", name: "India" },
                { code: "+86",  iso: "cn", name: "China" }
            ];

            function flagUrl(iso) {
                return ctx + "/images/flags/" + iso + ".svg";
            }

            function findCountry(code) {
                for (var i = 0; i < COUNTRIES.length; i++) {
                    if (COUNTRIES[i].code === code) return COUNTRIES[i];
                }
                return null;
            }

            function renderList(filter) {
                filter = (filter || "").trim().toLowerCase();
                list.innerHTML = "";

                for (var i = 0; i < COUNTRIES.length; i++) {
                    var c = COUNTRIES[i];
                    var haystack = (c.code + " " + c.name).toLowerCase();
                    if (filter === "" || haystack.indexOf(filter) !== -1) {
                        var li = document.createElement("li");
                        li.dataset.code = c.code;
                        li.dataset.iso  = c.iso;

                        var img = document.createElement("img");
                        img.src = flagUrl(c.iso);
                        img.alt = c.iso;
                        li.appendChild(img);

                        var codeSpan = document.createElement("span");
                        codeSpan.className = "cc-code";
                        codeSpan.textContent = c.code;
                        li.appendChild(codeSpan);

                        var nameSpan = document.createElement("span");
                        nameSpan.className = "cc-name";
                        nameSpan.textContent = c.name;
                        li.appendChild(nameSpan);

                        list.appendChild(li);
                    }
                }
            }

            function openList() {
                renderList(visibleInput.value);
                list.classList.add("open");
            }

            function closeList() {
                list.classList.remove("open");
            }

            function selectCountry(c) {
                visibleInput.value = c.code;
                hiddenInput.value  = c.code;
                flagImg.src        = flagUrl(c.iso);
                closeList();
            }

            visibleInput.addEventListener("focus", openList);
            visibleInput.addEventListener("input", openList);

            visibleInput.addEventListener("blur", function () {
                setTimeout(function () {
                    closeList();
                    var typed = visibleInput.value.trim();
                    var c = findCountry(typed);
                    if (c) {
                        selectCountry(c);
                    } else {
                        visibleInput.value = hiddenInput.value;
                    }
                }, 150);
            });

            list.addEventListener("mousedown", function (e) {
                var li = e.target.closest("li");
                if (!li) return;
                var c = findCountry(li.dataset.code);
                if (c) selectCountry(c);
            });

            var initial = findCountry(picker.dataset.selected);
            if (initial) {
                visibleInput.value = initial.code;
                hiddenInput.value  = initial.code;
                flagImg.src        = flagUrl(initial.iso);
            }
        }

		var picInput   = document.getElementById("profilePic");
		var picPreview = document.getElementById("profilePicPreviewImg");
		var picClear   = document.getElementById("profilePicClear");

		if (picInput && picPreview && picClear) {
		    var defaultPicSrc = picPreview.src;

		    picInput.addEventListener("change", function () {
		        var file = picInput.files && picInput.files[0];
		        if (!file) return;

		        if (file.size > 2 * 1024 * 1024) {
		            alert("Image is larger than 2 MB. Please choose a smaller file.");
		            picInput.value = "";
		            return;
		        }

		        var reader = new FileReader();
		        reader.onload = function (e) {
		            picPreview.src = e.target.result;
		        };
		        reader.readAsDataURL(file);
		    });

		    picClear.addEventListener("click", function () {
		        picInput.value = "";
		        picPreview.src = defaultPicSrc;
		    });
		}

        function evaluateStrength(pw) {
            var score = 0;

            if (pw.length >= 8)  score++;
            if (pw.length >= 12) score++;

            if (/[a-z]/.test(pw)) score++;
            if (/[A-Z]/.test(pw)) score++;
            if (/[0-9]/.test(pw)) score++;
            if (/[^A-Za-z0-9]/.test(pw)) score++;

            if (pw.length < 6) return "weak";

            if (score >= 5) return "strong";
            if (score >= 3) return "medium";
            return "weak";
        }

    });

    window.fillCategoryForm = function (button) {
        var idField = document.getElementById("catId");
        var nameField = document.getElementById("catName");
        var descField = document.getElementById("catDescription");

        if (idField) idField.value = button.getAttribute("data-id") || "";
        if (nameField) nameField.value = button.getAttribute("data-name") || "";
        if (descField) descField.value = button.getAttribute("data-description") || "";

        if (nameField) nameField.focus();
    };
})();