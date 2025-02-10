const apiBaseUrl = "/api/persons";
let editingPersonId = null;

function showPopupMessage(message, type = "success") {
    const popup = document.createElement("div");
    popup.className = `popup-message ${type}`;
    popup.textContent = message;
    document.body.appendChild(popup);
    setTimeout(() => popup.remove(), 3000);
}

// Ensure styles are loaded dynamically
document.addEventListener("DOMContentLoaded", () => {
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = "styles.css";
    document.head.appendChild(link);
});

async function fetchPersons() {
    try {
        const response = await fetch(apiBaseUrl);
        if (!response.ok) throw new Error("Failed to fetch persons");

        const persons = await response.json();
        const list = document.getElementById("personList");
        list.innerHTML = "";
        const existingEmails = new Set();

        persons.sort((a, b) => a.name.localeCompare(b.name));

        persons.forEach(({ id, name, email }) => {
            if (existingEmails.has(email)) return;
            existingEmails.add(email);

            const li = document.createElement("li");
            li.innerHTML = `
                <div class="person-info">
                    <strong>${name}</strong> <br> ${email}
                </div>
                <div>
                    <button class="edit" onclick="editPerson(${id}, '${name}', '${email}')">Edit</button>
                    <button class="delete" onclick="deletePerson(${id})">Delete</button>
                </div>
            `;
            list.appendChild(li);
        });

        return existingEmails;
    } catch (error) {
        console.error(error);
        showPopupMessage("Error fetching persons", "error");
    }
}

document.getElementById("addPersonForm").addEventListener("submit", async function (event) {
    event.preventDefault();
    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("email").value.trim();

    const namePattern = /^[A-Za-z\s]+$/;
    if (!name || !namePattern.test(name)) {
        return showPopupMessage("Name must contain only letters and spaces.", "error");
    }

    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (!email || !emailPattern.test(email)) {
        return showPopupMessage("Please enter a valid email address.", "error");
    }

    try {
        const existingEmails = await fetchPersons();

        if (existingEmails.has(email) && editingPersonId === null) {
            return showPopupMessage("Email already exists.", "error");
        }

        if (editingPersonId === null) {
            const response = await fetch(apiBaseUrl, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name, email })
            });

            if (response.ok) {
                document.getElementById("addPersonForm").reset();
                await fetchPersons();
                showPopupMessage("Person added successfully");
            } else {
                showPopupMessage("Error adding person", "error");
            }
        } else {
            await updatePerson(editingPersonId, name, email);
        }
    } catch (error) {
        console.error(error);
        showPopupMessage("An error occurred.", "error");
    }
});

async function deletePerson(id) {
    if (!confirm("Are you sure you want to delete this person?")) return;

    try {
        const response = await fetch(`${apiBaseUrl}/${id}`, { method: "DELETE" });
        if (response.ok) {
            await fetchPersons();
            showPopupMessage("Person deleted successfully");
        } else {
            showPopupMessage("Error deleting person", "error");
        }
    } catch (error) {
        console.error(error);
        showPopupMessage("An error occurred.", "error");
    }
}

function editPerson(id, name, email) {
    document.getElementById("name").value = name;
    document.getElementById("email").value = email;
    editingPersonId = id;
    document.getElementById("submitButton").textContent = "Update Person";

    const cancelBtn = document.getElementById("cancelEdit");
    if (!cancelBtn) {
        const button = document.createElement("button");
        button.id = "cancelEdit";
        button.textContent = "Cancel";
        button.addEventListener("click", resetForm);
        document.getElementById("formButtons").appendChild(button);
    }
}

function resetForm() {
    editingPersonId = null;
    document.getElementById("addPersonForm").reset();
    document.getElementById("submitButton").textContent = "Add Person";
    const cancelBtn = document.getElementById("cancelEdit");
    if (cancelBtn) cancelBtn.remove();
}

async function updatePerson(id, name, email) {
    try {
        const response = await fetch(`${apiBaseUrl}/${id}`, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email })
        });

        if (response.ok) {
            resetForm();
            await fetchPersons();
            showPopupMessage("Person updated successfully");
        } else {
            showPopupMessage("Error updating person", "error");
        }
    } catch (error) {
        console.error(error);
        showPopupMessage("An error occurred.", "error");
    }
}

// Excel and PDF downloads
document.getElementById("downloadExcel").addEventListener("click", () => {
    window.location.href = "/api/persons/download";
});

document.getElementById("downloadPdf").addEventListener("click", async () => {
    try {
        const response = await fetch("/api/persons/preview-pdf");
        const blob = await response.blob();
        const url = URL.createObjectURL(blob);
        window.open(url, "_blank");
    } catch (error) {
        console.error(error);
        showPopupMessage("Error downloading PDF", "error");
    }
});
fetchPersons();
