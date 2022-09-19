//package com.melita_task.melita;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//import com.melita_task.CustomerStructure.Customer;
//import com.melita_task.CustomerStructure.FullName;
//import com.melita_task.CustomerStructure.InstallationAddress;
//import org.junit.jupiter.api.Test;
//
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.ArrayList;
//
//@SpringBootTest
//class CustomerTests {
//
//    @Test
//    public void customerObjectCreate() {
//        final FullName fullName = FullName.builder()
//                .firstName("First Name")
//                .middleName("Middle Name")
//                .lastName("Last Name")
//                .build();
//
//        final InstallationAddress installationAddress = InstallationAddress.builder()
//                .island("Island")
//                .town("Town")
//                .street("Street")
//                .building("Building")
//                .build();
//
//        Customer customer = Customer
//                .builder()
//                .id(1)
//                .fullname(fullName)
//                .installationAddress(installationAddress)
//                .services(new ArrayList<>())
//                .build();
//
//        assertThat(customer).isNotNull();
//        assertThat(customer.getFullname().getFirstName()).isEqualTo("First Name");
//        assertThat(customer.getFullname().getMiddleName()).isEqualTo("Middle Name");
//        assertThat(customer.getFullname().getLastName()).isEqualTo("Last Name");
//        assertThat(customer.getInstallationAddress().getIsland()).isEqualTo("Island");
//        assertThat(customer.getInstallationAddress().getTown()).isEqualTo("Town");
//        assertThat(customer.getInstallationAddress().getStreet()).isEqualTo("Street");
//        assertThat(customer.getInstallationAddress().getBuilding()).isEqualTo("Building");
//
//    }
//
//
//    @Test
//    public void customerObjectUpdate() {
//
//        final FullName fullName = FullName.builder()
//                .firstName("First Name")
//                .middleName("Middle Name")
//                .lastName("Last Name")
//                .build();
//
//        final InstallationAddress installationAddress = InstallationAddress.builder()
//                .island("Island")
//                .town("Town")
//                .street("Street")
//                .building("Building")
//                .build();
//
//        Customer customer = Customer
//                .builder()
//                .id(1)
//                .fullname(fullName)
//                .installationAddress(installationAddress)
//                .services(new ArrayList<>())
//                .build();
//
//        assertThat(customer).isNotNull();
//
//        final String name = "Customer-Name";
//
//        final String surname = "Customer-Surname";
//
//        final String street = "Customer-Street";
//
//        customer.updateCustomer(name, null, surname, null, null, street, null);
//
//        assertThat(customer.getFullname().getFirstName()).isEqualTo(name);
//        assertThat(customer.getFullname().getLastName()).isEqualTo(surname);
//        assertThat(customer.getInstallationAddress().getStreet()).isEqualTo(street);
//    }
//
//
//    final FullName fullName = FullName.builder()
//            .firstName("First Name")
//            .middleName("Middle Name")
//            .lastName("Last Name")
//            .build();
//
//    final InstallationAddress installationAddress = InstallationAddress.builder()
//            .island("Island")
//            .town("Town")
//            .street("Street")
//            .building("Building")
//            .build();
//
//    Customer customer = Customer
//            .builder()
//            .id(1)
//            .fullname(fullName)
//            .installationAddress(installationAddress)
//            .services(new ArrayList<>())
//            .build();
//
//    @Test
//    public void customerObjectAttachServiceGetService(){
//        customer.attachService(Service.builder().serviceId(100).lobType(Services.INT).build());
//        assertThat(customer.getServices()).isNotEmpty();
//    }
//
//}