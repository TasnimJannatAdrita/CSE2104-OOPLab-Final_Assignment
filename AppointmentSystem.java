/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.appointmentsystem;


import java.io.*;
import java.util.*;

abstract class Doctor {
    private String ID;
    private String Name;
    private String Specialization;
    
    public Doctor(String id, String name, String specialization) {
        this.ID = id;
        this.Name = name;
        this.Specialization = specialization;
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    public String getSpecialization() {
        return Specialization;
    }

    public abstract void displayAvailability();

    @Override
    public String toString() {
        return ID + "," + Name + "," + Specialization;
    }
}

class GeneralPractitioner extends Doctor {
    public GeneralPractitioner(String id, String name) {
        super(id, name, "General Practitioner");
    }

    @Override
    public void displayAvailability() {
        System.out.println("Walk-in appointments available.");
    }
}

class Specialist extends Doctor {
    public Specialist(String id, String name) {
        super(id, name, "Specialist");
    }

    @Override
    public void displayAvailability() {
        System.out.println("Appointments require prior confirmation.");
    }
}

class Patient {
    private String ID;
    private String Name;

    public Patient(String id, String name) {
        this.ID = id;
        this.Name = name;
    }

    public String getId() {
        return ID;
    }

    public String getName() {
        return Name;
    }

    @Override
    public String toString() {
        return ID + "," + Name;
    }
}

class Appointment {
    private Patient Patient;
    private Doctor Doctor;
    private String Date;
    private String Time;

    public Appointment(Patient patient, Doctor doctor, String date, String time) {
        this.Patient = patient;
        this.Doctor = doctor;
        this.Date = date;
        this.Time = time;
    }

    @Override
    public String toString() {
        return Patient.getId() + "," + Doctor.getId() + "," + Date + "," + Time;
    }
}

public class AppointmentSystem {
    private static final String DOCTORS_FILE = "doctors.txt";
    private static final String PATIENTS_FILE = "patients.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";

    private static List<Doctor> doctors = new ArrayList<>();
    private static List<Patient> patients = new ArrayList<>();
    private static List<Appointment> appointments = new ArrayList<>();

    public static void main(String[] args) {
        loadDoctors();
        loadPatients();
        loadAppointments();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. View Doctors");
            System.out.println("2. Register Patient");
            System.out.println("3. Book Appointment");
            System.out.println("4. View Appointments");
            System.out.println("5. Exit");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    viewDoctors();
                    break;
                case 2:
                    registerPatient(scanner);
                    break;
                case 3:
                    bookAppointment(scanner);
                    break;
                case 4:
                    viewAppointments();
                    break;
                case 5:
                    saveDoctors();
                    savePatients();
                    saveAppointments();
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void loadDoctors() {
        try (BufferedReader br = new BufferedReader(new FileReader(DOCTORS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[2].equals("General Practitioner")) {
                    doctors.add(new GeneralPractitioner(parts[0], parts[1]));
                } else {
                    doctors.add(new Specialist(parts[0], parts[1]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading doctors.");
        }
    }

    private static void saveDoctors() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DOCTORS_FILE))) {
            for (Doctor doctor : doctors) {
                bw.write(doctor.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving doctors.");
        }
    }

    private static void loadPatients() {
        try (BufferedReader br = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                patients.add(new Patient(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("Error loading patients.");
        }
    }

    private static void savePatients() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATIENTS_FILE))) {
            for (Patient patient : patients) {
                bw.write(patient.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving patients.");
        }
    }

    private static void loadAppointments() {
        try (BufferedReader br = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Patient patient = findPatient(parts[0]);
                Doctor doctor = findDoctor(parts[1]);
                if (patient != null && doctor != null) {
                    appointments.add(new Appointment(patient, doctor, parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading appointments.");
        }
    }

    private static void saveAppointments() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(APPOINTMENTS_FILE))) {
            for (Appointment appointment : appointments) {
                bw.write(appointment.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving appointments.");
        }
    }

    private static void viewDoctors() {
        for (Doctor doctor : doctors) {
            System.out.println("ID: " + doctor.getId() + ", Name: " + doctor.getName() + 
              ", Specialization: " + doctor.getSpecialization());
            doctor.displayAvailability();
        }
    }

    private static void registerPatient(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String id = scanner.nextLine();
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        patients.add(new Patient(id, name));
        System.out.println("Patient registered successfully.");
    }

    private static void bookAppointment(Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        String patientId = scanner.nextLine();
        Patient patient = findPatient(patientId);
        if (patient == null) {
            System.out.println("Patient not found.");
            return;
        }

        System.out.print("Enter Doctor ID: ");
        String doctorId = scanner.nextLine();
        Doctor doctor = findDoctor(doctorId);
        if (doctor == null) {
            System.out.println("Doctor not found.");
            return;
        }

        System.out.print("Enter Date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter Time (HH:MM): ");
        String time = scanner.nextLine();

        appointments.add(new Appointment(patient, doctor, date, time));
        saveAppointments();
        System.out.println("Appointment booked successfully.");
    }

    private static void viewAppointments() {
        for (Appointment appointment : appointments) {
            System.out.println(appointment);
        }
    }

    private static Patient findPatient(String id) {
        for (Patient patient : patients) {
            if (patient.getId().equals(id)) {
                return patient;
            }
        }
        return null;
    }

    private static Doctor findDoctor(String id) {
        for (Doctor doctor : doctors) {
            if (doctor.getId().equals(id)) {
                return doctor;
            }
        }
        return null;
    }
}


