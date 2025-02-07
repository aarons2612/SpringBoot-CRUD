const apiBaseUrl = "/api/persons";
let editingPersonId = null;

function showPopupMessage(message, type = "success") {
    const popup = document.createElement("div");
    popup.className = `popup-message ${type}`;
    popup.textContent = message;
    document.body.appendChild(popup);
    setTimeout(() => {
        popup.remove();
    }, 3000);
}

document.addEventListener("DOMContentLoaded", () => {
    const link = document.createElement("link");
    link.rel = "stylesheet";
    link.href = "styles.css";
    document.head.appendChild(link);
});

async function fetchPersons() {
    const response = await fetch(apiBaseUrl);
    const persons = await response.json();
    const list = document.getElementById("personList");
    list.innerHTML = "";
    const existingEmails = new Set();
    persons.sort((a, b) => a.name.localeCompare(b.name));
    persons.forEach(person => {
        if (existingEmails.has(person.email)) return;
        existingEmails.add(person.email);
        const li = document.createElement("li");
        li.innerHTML = `
            <div class="person-info">
                <strong>${person.name}</strong> <br> ${person.email}
            </div>
            <div>
                <button class="edit" onclick="editPerson(${person.id}, '${person.name}', '${person.email}')">Edit</button>
                <button class="delete" onclick="deletePerson(${person.id})">Delete</button>
            </div>
        `;
        list.appendChild(li);
    });
    return existingEmails;
}

document.getElementById("addPersonForm").addEventListener("submit", async function(event) {
    event.preventDefault();
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const namePattern = /^[A-Za-z\s]+$/;
    if (!name || !namePattern.test(name)) {
        showPopupMessage("Name must contain only letters and spaces.", "error");
        return;
    }
    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z.]+\.[a-zA-Z]{2,6}$/;
    if (!email || !emailPattern.test(email)) {
        showPopupMessage("Please enter a valid email address.", "error");
        return;
    }
    const existingEmails = await fetchPersons();
    if (existingEmails.has(email) && editingPersonId === null) {
        showPopupMessage("Email already exists.", "error");
        return;
    } else if (existingEmails.has(email) && editingPersonId !== null) {
        const currentPerson = await fetch(`${apiBaseUrl}/${editingPersonId}`);
        const personData = await currentPerson.json();
        if (email !== personData.email) {
            showPopupMessage("Email already exists.", "error");
            return;
        }
    }
    if (editingPersonId === null) {
        const response = await fetch(apiBaseUrl, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ name, email })
        });
        if (response.ok) {
            document.getElementById("addPersonForm").reset();
            fetchPersons();
            showPopupMessage("Person added successfully");
        } else {
            showPopupMessage("Error adding person", "error");
        }
    } else {
        updatePerson(editingPersonId, name, email);
    }
});

async function deletePerson(id) {
    if (confirm("Are you sure you want to delete this person?")) {
        const response = await fetch(`${apiBaseUrl}/${id}`, { method: "DELETE" });
        if (response.ok) {
            fetchPersons();
            showPopupMessage("Person deleted successfully");
        } else {
            showPopupMessage("Error deleting person", "error");
        }
    }
}

function editPerson(id, name, email) {
    document.getElementById("name").value = name;
    document.getElementById("email").value = email;
    editingPersonId = id;
    document.getElementById("submitButton").textContent = "Update Person";
}

async function updatePerson(id, name, email) {
    const response = await fetch(`${apiBaseUrl}/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name, email })
    });
    if (response.ok) {
        editingPersonId = null;
        document.getElementById("addPersonForm").reset();
        document.getElementById("submitButton").textContent = "Add Person";
        fetchPersons();
        showPopupMessage("Person updated successfully");
    } else {
        showPopupMessage("Error updating person", "error");
    }
}

document.getElementById("downloadExcel").addEventListener("click", function () {
    window.location.href = "/api/persons/download"; // Backend API for Excel
});

document.getElementById("downloadPdf").addEventListener("click", function () {
    window.location.href = "/api/persons/download/pdf"; // Backend API for PDF
});


fetchPersons();
