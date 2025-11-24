// Demo data storage
let currentUser = null;
let users = [
    { id: 1, name: "Admin User", email: "admin@petadoption.com", password: "admin123", role: "admin" },
    { id: 2, name: "Happy Paws Shelter", email: "shelter@happypaws.com", password: "shelter123", role: "shelter" },
    { id: 3, name: "John Adopter", email: "john@email.com", password: "john123", role: "adopter" },
    { id: 4, name: "Sarah Adopter", email: "sarah@email.com", password: "sarah123", role: "adopter" }
];

let pets = [
    { id: 1, name: "Max", type: "Dog", breed: "Golden Retriever", age: 3, shelterId: 2, description: "Friendly and energetic", status: "available", approval: "approved" },
    { id: 2, name: "Luna", type: "Cat", breed: "Siamese", age: 2, shelterId: 2, description: "Calm and affectionate", status: "available", approval: "approved" },
    { id: 3, name: "Buddy", type: "Dog", breed: "Labrador", age: 4, shelterId: 2, description: "Loyal and playful", status: "available", approval: "pending" }
];

let applications = [
    { id: 1, adopterId: 3, petId: 1, status: "submitted", notes: "I love dogs and have a large backyard" },
    { id: 2, adopterId: 4, petId: 2, status: "submitted", notes: "Always wanted a cat" }
];

let nextPetId = 4;
let nextApplicationId = 3;

// LOGIN FUNCTIONALITY
document.getElementById("loginForm").addEventListener("submit", function(event) {
    event.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    
    const user = users.find(u => u.email === email && u.password === password);
    
    if (user) {
        currentUser = user;
        loadDashboard(user.role);
    } else {
        alert("Invalid email or password!");
    }
});

// LOAD APPROPRIATE DASHBOARD
function loadDashboard(role) {
    document.getElementById("loginPage").style.display = "none";
    
    switch(role) {
        case "admin":
            loadAdminDashboard();
            document.getElementById("adminDashboard").style.display = "block";
            break;
        case "shelter":
            loadShelterDashboard();
            document.getElementById("shelterDashboard").style.display = "block";
            break;
        case "adopter":
            loadAdopterDashboard();
            document.getElementById("adopterDashboard").style.display = "block";
            break;
    }
}

// LOGOUT
function logout() {
    currentUser = null;
    document.getElementById("loginPage").style.display = "block";
    document.getElementById("adminDashboard").style.display = "none";
    document.getElementById("shelterDashboard").style.display = "none";
    document.getElementById("adopterDashboard").style.display = "none";
    document.getElementById("loginForm").reset();
}

// ============ ADMIN DASHBOARD ============
function loadAdminDashboard() {
    // Update stats
    document.getElementById("adminTotalUsers").textContent = users.length;
    document.getElementById("adminTotalPets").textContent = pets.length;
    document.getElementById("adminPendingPets").textContent = pets.filter(p => p.approval === "pending").length;
    document.getElementById("adminTotalApps").textContent = applications.length;
    
    // Load users list
    let usersList = "<ul class='list-group'>";
    users.forEach(user => {
        usersList += `<li class='list-group-item d-flex justify-content-between align-items-center'>
            ${user.name} (${user.role})
            <span class='badge badge-primary badge-pill'>${user.email}</span>
        </li>`;
    });
    usersList += "</ul>";
    document.getElementById("adminUsersList").innerHTML = usersList;
    
    // Load pending pets
    let pendingList = "<div class='row'>";
    pets.filter(p => p.approval === "pending").forEach(pet => {
        pendingList += `<div class='col-md-12 mb-3'>
            <div class='card'>
                <div class='card-body'>
                    <h5>${pet.name} - ${pet.type} (${pet.breed})</h5>
                    <p>${pet.description}</p>
                    <button class='btn btn-success btn-sm' onclick='approvePet(${pet.id})'>Approve</button>
                    <button class='btn btn-danger btn-sm' onclick='rejectPet(${pet.id})'>Reject</button>
                </div>
            </div>
        </div>`;
    });
    pendingList += "</div>";
    document.getElementById("adminPendingList").innerHTML = pendingList;
}

function approvePet(petId) {
    const pet = pets.find(p => p.id === petId);
    if (pet) {
        pet.approval = "approved";
        alert("✓ Pet listing approved!");
        loadAdminDashboard();
    }
}

function rejectPet(petId) {
    const pet = pets.find(p => p.id === petId);
    if (pet) {
        pet.approval = "rejected";
        alert("✓ Pet listing rejected!");
        loadAdminDashboard();
    }
}

// ============ SHELTER DASHBOARD ============
function loadShelterDashboard() {
    // Load shelter's pets
    let shelterPets = pets.filter(p => p.shelterId === currentUser.id);
    let petsList = "<div class='row'>";
    
    shelterPets.forEach(pet => {
        let statusBadge = `<span class='badge badge-${pet.status === "available" ? "success" : "danger"}'>${pet.status}</span>`;
        let approvalBadge = `<span class='badge badge-${pet.approval === "approved" ? "success" : pet.approval === "pending" ? "warning" : "danger"}'>${pet.approval}</span>`;
        
        petsList += `<div class='col-md-6 mb-3'>
            <div class='card'>
                <div class='card-body'>
                    <h5>${pet.name}</h5>
                    <p><strong>Type:</strong> ${pet.type}</p>
                    <p><strong>Breed:</strong> ${pet.breed}</p>
                    <p><strong>Age:</strong> ${pet.age} years</p>
                    <p>${pet.description}</p>
                    <p>Status: ${statusBadge} | Approval: ${approvalBadge}</p>
                </div>
            </div>
        </div>`;
    });
    
    if (shelterPets.length === 0) {
        petsList += "<p>You haven't listed any pets yet.</p>";
    }
    petsList += "</div>";
    document.getElementById("shelterPetsList").innerHTML = petsList;
    
    // Load applications
    let appsList = "<div class='row'>";
    applications.forEach(app => {
        const pet = pets.find(p => p.id === app.petId);
        const adopter = users.find(u => u.id === app.adopterId);
        
        if (pet && pet.shelterId === currentUser.id) {
            appsList += `<div class='col-md-12 mb-3'>
                <div class='card'>
                    <div class='card-body'>
                        <h5>${pet.name} - Application from ${adopter.name}</h5>
                        <p><strong>Status:</strong> ${app.status}</p>
                        <p><strong>Notes:</strong> ${app.notes}</p>
                        <button class='btn btn-success btn-sm' onclick='updateAppStatus(${app.id}, "approved")'>Approve</button>
                        <button class='btn btn-danger btn-sm' onclick='updateAppStatus(${app.id}, "rejected")'>Reject</button>
                        <button class='btn btn-info btn-sm' onclick='updateAppStatus(${app.id}, "adopted")'>Mark as Adopted</button>
                    </div>
                </div>
            </div>`;
        }
    });
    
    if (appsList === "<div class='row'>") {
        appsList += "<p>No applications yet.</p></div>";
    } else {
        appsList += "</div>";
    }
    document.getElementById("shelterApplicationsList").innerHTML = appsList;
    
    // Update statistics
    document.getElementById("shelterTotalPets").textContent = shelterPets.length;
    document.getElementById("shelterAdoptedPets").textContent = shelterPets.filter(p => p.status === "adopted").length;
    
    let shelterAppsCount = applications.filter(app => {
        const pet = pets.find(p => p.id === app.petId);
        return pet && pet.shelterId === currentUser.id;
    }).length;
    document.getElementById("shelterApplicationCount").textContent = shelterAppsCount;
    
    // Add pet form
    document.getElementById("addPetForm").onsubmit = function(e) {
        e.preventDefault();
        const form = this;
        const newPet = {
            id: nextPetId++,
            name: form.querySelector('input[placeholder*="Max"]').value,
            type: form.querySelector('select').value,
            breed: form.querySelector('input[placeholder*="Golden"]').value,
            age: parseInt(form.querySelector('input[placeholder*="3"]').value),
            shelterId: currentUser.id,
            description: form.querySelector('textarea').value,
            status: "available",
            approval: "pending"
        };
        
        pets.push(newPet);
        alert("✓ Pet listing submitted!");
        form.reset();
        loadShelterDashboard();
    };
}

function updateAppStatus(appId, newStatus) {
    const app = applications.find(a => a.id === appId);
    if (app) {
        app.status = newStatus;
        if (newStatus === "adopted") {
            const pet = pets.find(p => p.id === app.petId);
            if (pet) pet.status = "adopted";
        }
        alert("✓ Application status updated!");
        loadShelterDashboard();
    }
}

// ============ ADOPTER DASHBOARD ============
function loadAdopterDashboard() {
    // Load available pets
    let availablePets = pets.filter(p => p.status === "available" && p.approval === "approved");
    let petsList = "<div class='row'>";
    
    availablePets.forEach(pet => {
        const shelter = users.find(u => u.id === pet.shelterId);
        petsList += `<div class='col-md-6 mb-3'>
            <div class='card'>
                <div class='card-body'>
                    <h5>${pet.name}</h5>
                    <p><strong>Type:</strong> ${pet.type}</p>
                    <p><strong>Breed:</strong> ${pet.breed}</p>
                    <p><strong>Age:</strong> ${pet.age} years</p>
                    <p>${pet.description}</p>
                    <p><small>From: ${shelter ? shelter.name : "Unknown"}</small></p>
                    <button class='btn btn-primary btn-sm' onclick='applyForPet(${pet.id})'>Apply for Adoption</button>
                </div>
            </div>
        </div>`;
    });
    
    if (availablePets.length === 0) {
        petsList += "<p>No pets available right now.</p>";
    }
    petsList += "</div>";
    document.getElementById("availablePetsList").innerHTML = petsList;
    
    // Load my applications
    let myApps = applications.filter(a => a.adopterId === currentUser.id);
    let appsList = "<div class='row'>";
    
    myApps.forEach(app => {
        const pet = pets.find(p => p.id === app.petId);
        const shelter = users.find(u => u.id === pet.shelterId);
        
        appsList += `<div class='col-md-12 mb-3'>
            <div class='card'>
                <div class='card-body'>
                    <h5>${pet.name}</h5>
                    <p><strong>Status:</strong> <span class='badge badge-${app.status === "approved" ? "success" : app.status === "rejected" ? "danger" : "warning"}'>${app.status}</span></p>
                    <p><strong>Your Notes:</strong> ${app.notes}</p>
                    <p><strong>Shelter:</strong> ${shelter.name}</p>
                </div>
            </div>
        </div>`;
    });
    
    if (myApps.length === 0) {
        appsList += "<p>You haven't submitted any applications yet.</p>";
    }
    appsList += "</div>";
    document.getElementById("myApplicationsList").innerHTML = appsList;
    
    // Load profile
    let profileHtml = `
        <div class="form-group">
            <label>Name</label>
            <input type="text" class="form-control" value="${currentUser.name}" readonly>
        </div>
        <div class="form-group">
            <label>Email</label>
            <input type="email" class="form-control" value="${currentUser.email}" readonly>
        </div>
        <div class="form-group">
            <label>Role</label>
            <input type="text" class="form-control" value="Adopter" readonly>
        </div>
        <p><small>Account created and ready to adopt!</small></p>
    `;
    document.getElementById("profileContent").innerHTML = profileHtml;
}

function applyForPet(petId) {
    const pet = pets.find(p => p.id === petId);
    const notes = prompt(`Why would you like to adopt ${pet.name}?`);
    
    if (notes) {
        const newApp = {
            id: nextApplicationId++,
            adopterId: currentUser.id,
            petId: petId,
            status: "submitted",
            notes: notes
        };
        
        applications.push(newApp);
        alert("✓ Application submitted successfully!");
        loadAdopterDashboard();
    }
}

function searchPets() {
    const searchType = document.getElementById("searchType").value.toLowerCase();
    const searchBreed = document.getElementById("searchBreed").value.toLowerCase();
    
    let filtered = pets.filter(p => 
        p.status === "available" && 
        p.approval === "approved" &&
        (searchType === "" || p.type.toLowerCase().includes(searchType)) &&
        (searchBreed === "" || p.breed.toLowerCase().includes(searchBreed))
    );
    
    let petsList = "<div class='row'>";
    filtered.forEach(pet => {
        const shelter = users.find(u => u.id === pet.shelterId);
        petsList += `<div class='col-md-6 mb-3'>
            <div class='card'>
                <div class='card-body'>
                    <h5>${pet.name}</h5>
                    <p><strong>Type:</strong> ${pet.type}</p>
                    <p><strong>Breed:</strong> ${pet.breed}</p>
                    <p><strong>Age:</strong> ${pet.age} years</p>
                    <p>${pet.description}</p>
                    <p><small>From: ${shelter ? shelter.name : "Unknown"}</small></p>
                    <button class='btn btn-primary btn-sm' onclick='applyForPet(${pet.id})'>Apply for Adoption</button>
                </div>
            </div>
        </div>`;
    });
    
    if (filtered.length === 0) {
        petsList += "<p>No pets match your search.</p>";
    }
    petsList += "</div>";
    document.getElementById("availablePetsList").innerHTML = petsList;
}