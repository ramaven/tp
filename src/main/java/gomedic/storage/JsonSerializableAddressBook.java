package gomedic.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import gomedic.commons.exceptions.IllegalValueException;
import gomedic.model.AddressBook;
import gomedic.model.ReadOnlyAddressBook;
import gomedic.model.activity.Activity;
import gomedic.model.person.Person;
import gomedic.model.person.doctor.Doctor;
import gomedic.model.person.patient.Patient;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_ACTIVITY = "Activities list contains duplicate activities(s).";
    public static final String MESSAGE_DUPLICATE_DOCTOR = "Doctors list contains duplicate doctors(s).";
    public static final String MESSAGE_DUPLICATE_PATIENT = "Patients list contains duplicate patients(s).";

    // TODO remove ab3 person list
    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedActivity> activities = new ArrayList<>();
    private final List<JsonAdaptedDoctor> doctors = new ArrayList<>();
    private final List<JsonAdaptedPatient> patients = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given activities, doctors and patients
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
                                       @JsonProperty("activities") List<JsonAdaptedActivity> activities,
                                       @JsonProperty("doctors") List<JsonAdaptedDoctor> doctors,
                                       @JsonProperty("patients") List<JsonAdaptedPatient> patients) {
        this.persons.addAll(persons);
        this.activities.addAll(activities);
        this.doctors.addAll(doctors);
        this.patients.addAll(patients);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        activities.addAll(source
                .getActivityListSortedById()
                .stream()
                .map(JsonAdaptedActivity::new).collect(Collectors.toList()));
        doctors.addAll(source.getDoctorList().stream().map(JsonAdaptedDoctor::new).collect(Collectors.toList()));
        patients.addAll(source.getPatientList().stream().map(JsonAdaptedPatient::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }

        for (JsonAdaptedDoctor jsonAdaptedDoctor: doctors) {
            Doctor doctor = jsonAdaptedDoctor.toModelType();
            if (addressBook.hasDoctor(doctor)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_DOCTOR);
            }
            addressBook.addDoctor(doctor);
        }

        for (JsonAdaptedPatient jsonAdaptedPatient: patients) {
            Patient patient = jsonAdaptedPatient.toModelType();
            if (addressBook.hasPatient(patient)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PATIENT);
            }
            addressBook.addPatient(patient);
        }

        for (JsonAdaptedActivity jsonAdaptedActivity : activities) {
            Activity activity = jsonAdaptedActivity.toModelType();

            if (addressBook.hasActivity(activity)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_ACTIVITY);
            }
            addressBook.addActivity(activity);
        }

        return addressBook;
    }
}
