const API = axios.create({
    baseURL: "/api",
});

const departmentApi = {
    list() {
        return API.get("/departments");
    },
    detail(id) {
        return API.get(`/departments/${id}/staffs`);
    },
};

const staffApi = {
    list() {
        return API.get("/staffs");
    },
    detail(id) {
        return API.get(`/staffs/${id}`);
    },
    create(data) {
        return API.post("/staffs", data);
    },
    update(id, data) {
        return API.put(`/staffs/${id}`, data);
    },
    remove(id) {
        return API.delete(`/staffs/${id}`);
    },
};
