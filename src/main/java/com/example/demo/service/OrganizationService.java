package com.example.demo.service;

import com.example.demo.model.Department;
import com.example.demo.model.Staff;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrganizationService {

    // Single source of truth
    private final List<Department> departmentList = new ArrayList<>();
    private final AtomicLong staffIdCounter = new AtomicLong(1);

    public OrganizationService() {
        // Seed mock departments
        departmentList.add(new Department(101L, "Engineering"));
        departmentList.add(new Department(102L, "Human Resources"));

        // Seed initial staff
        createStaff(new Staff(null, "Alice Smith", "alice@example.com", 101L));
        createStaff(new Staff(null, "Bob Jones", "bob@example.com", 101L));
        createStaff(new Staff(null, "Charlie Brown", "charlie@example.com", 102L));
    }

    // Helper: Find department by ID
    private Optional<Department> findDepartmentById(Long departmentId) {
        return departmentList.stream()
                .filter(dept -> dept.getId().equals(departmentId))
                .findFirst();
    }

    // --- STAFF CRUD OPERATIONS ---

    // Get ALL staff across all departments
    public List<Staff> getAllStaff() {
        List<Staff> allStaff = new ArrayList<>();
        for (Department dept : departmentList) {
            allStaff.addAll(dept.getStaffs());
        }
        return allStaff;
    }

    // Get a single staff by ID
    public Optional<Staff> getStaffById(Long staffId) {
        for (Department dept : departmentList) {
            for (Staff staff : dept.getStaffs()) {
                if (staff.getId().equals(staffId)) {
                    return Optional.of(staff);
                }
            }
        }
        return Optional.empty();
    }

    // CREATE staff: Add to specified Department's staff list
    public Optional<Staff> createStaff(Staff staff) {
        Optional<Department> deptOpt = findDepartmentById(staff.getDepartmentId());
        if (deptOpt.isEmpty()) {
            return Optional.empty(); // Department not found
        }

        staff.setId(staffIdCounter.getAndIncrement());
        deptOpt.get().getStaffs().add(staff);
        return Optional.of(staff);
    }

    // UPDATE staff: Handles field updates and moving staff if departmentId changes
    public Optional<Staff> updateStaff(Long id, Staff updatedStaff) {
        // 1. Check if the new department exists
        Optional<Department> targetDeptOpt = findDepartmentById(updatedStaff.getDepartmentId());
        if (targetDeptOpt.isEmpty()) {
            return Optional.empty();
        }

        // 2. Find existing staff and current department
        for (Department dept : departmentList) {
            List<Staff> deptStaffs = dept.getStaffs();
            for (int i = 0; i < deptStaffs.size(); i++) {
                Staff existing = deptStaffs.get(i);
                if (existing.getId().equals(id)) {
                    
                    updatedStaff.setId(id);

                    // If department changed, move staff to new department list
                    if (!existing.getDepartmentId().equals(updatedStaff.getDepartmentId())) {
                        deptStaffs.remove(i); // Remove from old department
                        targetDeptOpt.get().getStaffs().add(updatedStaff); // Add to new department
                    } else {
                        deptStaffs.set(i, updatedStaff); // Update in-place
                    }
                    return Optional.of(updatedStaff);
                }
            }
        }
        return Optional.empty(); // Staff ID not found
    }

    // DELETE staff: Removes staff directly from its department list
    public boolean deleteStaff(Long staffId) {
        for (Department dept : departmentList) {
            boolean removed = dept.getStaffs().removeIf(s -> s.getId().equals(staffId));
            if (removed) {
                return true;
            }
        }
        return false;
    }

    // --- DEPARTMENT OPERATIONS ---

    public List<Department> getAllDepartments() {
        return new ArrayList<>(departmentList);
    }

    public Optional<Department> getDepartmentWithStaffs(Long departmentId) {
        return findDepartmentById(departmentId);
    }
}