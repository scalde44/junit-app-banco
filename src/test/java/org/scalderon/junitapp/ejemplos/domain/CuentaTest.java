package org.scalderon.junitapp.ejemplos.domain;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.scalderon.junitapp.ejemplos.exception.DineroInsuficienteException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumingThat;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CuentaTest {
    private Cuenta cuenta;
    private TestInfo testInfo;
    private TestReporter testReporter;

    @BeforeAll
    static void beforeAll() {
        System.out.println("Inicializando la clase CuentaTest");
    }

    @BeforeEach
    void setUp(TestInfo testInfo, TestReporter testReporter) {
        cuenta = new Cuenta("Steven", new BigDecimal("123.45235"), null);
        System.out.println("Iniciando metodo");
        this.testInfo = testInfo;
        this.testReporter = testReporter;
        this.testReporter.publishEntry("Ejecutando: " + testInfo.getDisplayName() + " " + testInfo.getTestMethod().get().getName() + " con las etiquetas " + testInfo.getTags());
    }

    @AfterEach
    void tearDown() {
        System.out.println("Finalizando metodo");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("Finalizando clase CuentaTest");
    }

    @Test
    @DisplayName("Este test sirve para comprobar la asignacion del nombre de la cuenta")
    @Disabled
    void debeCrearNombreCuenta() {

        String nombreEsperado = "Steven";
        String nombreReal = this.cuenta.getPersona();

        assertEquals(nombreEsperado, nombreReal, () -> "El nombre no es el que se esperaba: Se esperaba "
                + nombreEsperado + " sin embargo fue: " + nombreReal);
    }

    @Tag("cuenta")
    @Test
    @DisplayName("Este test sirve para comprobar la asignacion del saldo de la cuenta")
    void debeCrearUnSaldoCuenta() {

        Double saldoEsperado = 123.45235;
        Double saldoReal = this.cuenta.getSaldo().doubleValue();
        assertEquals(saldoEsperado, saldoReal);
        assertFalse(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @Test
    @DisplayName("Este test sirve para comprobar que se compare y referencia una cuenta con otra")
    void debeReferenciarUnaCuenta() {
        Cuenta cuenta2 = new Cuenta("Steven", new BigDecimal("123.45235"), null);

        //assertNotEquals(cuenta2, cuenta);
        assertEquals(cuenta2, this.cuenta);
    }

    @Tag("cuenta")
    @Test
    @DisplayName("Este test sirve para comprobar el debido de una cuenta")
    void debeCrearUnDebitoCuenta() {
        this.cuenta.debito(new BigDecimal(100));
        assertNotNull(this.cuenta.getSaldo());
        assertEquals(23, this.cuenta.getSaldo().intValue());
        assertEquals("23.45235", this.cuenta.getSaldo().toPlainString());
    }

    @Tag("cuenta")
    @Test
    @DisplayName("Este test sirve para comprobar el credito de una cuenta")
    void debeCrearUnCreditoCuenta() {
        this.cuenta.credito(new BigDecimal(100));
        assertNotNull(this.cuenta.getSaldo());
        assertEquals(223, this.cuenta.getSaldo().intValue());
        assertEquals("223.45235", this.cuenta.getSaldo().toPlainString());
    }

    @Tag("excepcion")
    @Test
    @DisplayName("Este test sirve para comprobar la excepcion lanzada cuando hay dinero insuficiente para debitar")
    void debeLanzarExcepcionPorDineroInsuficiente() {

        Exception exception = assertThrows(DineroInsuficienteException.class, () -> {
            this.cuenta.debito(new BigDecimal(200));
        });
        String mensajeReal = exception.getMessage();
        String mensajeEsperado = "Dinero insuficiente";
        assertEquals(mensajeEsperado, mensajeReal);
    }

    @Test
    @DisplayName("Este test sirve para comprobar la transferencia de dinero entre cuentas")
    void debeTransferirDineroCuentas() {
        Cuenta cuenta2 = new Cuenta("Steven Calderon", new BigDecimal("1500.8989"), null);
        Banco banco = new Banco("Bancolombia");
        banco.transferir(cuenta2, this.cuenta, new BigDecimal(500));
        assertEquals("1000.8989", cuenta2.getSaldo().toPlainString());
        assertEquals("623.45235", this.cuenta.getSaldo().toPlainString());
    }

    @Tag("cuenta")
    @Test
    @DisplayName("Este test sirve para comprobar que se le asigne un banco a las cuentas")
    void debeRelacionarBancoConCuentas() {
        Cuenta cuenta2 = new Cuenta("Steven", new BigDecimal("1500.8989"), null);
        Banco banco = new Banco("Bancolombia");
        banco.addCuenta(cuenta);
        banco.addCuenta(cuenta2);
        assertAll(() -> {
            assertEquals(2, banco.getCuentas().size());
        }, () -> {
            assertEquals("Bancolombia", this.cuenta.getNombreBanco());
        }, () -> {
            assertEquals("Steven", banco.buscarCuentaPorNombre("Steven"));
        });
    }

    @Tag("operativo")
    @Nested
    class SistemaOperativoTest {
        @Test
        @EnabledOnOs(OS.WINDOWS)
        void debeEjecutarseSoloEnWindows() {
        }

        @Test
        @EnabledOnOs(OS.LINUX)
        void debeEjecutarseSoloEnLinux() {
        }

        @Test
        @EnabledOnOs(OS.MAC)
        void debeEjecutarseSoloEnMac() {
        }

        @Test
        @DisabledOnOs(OS.WINDOWS)
        void noDebeEjecutarseEnWindows() {
        }
    }

    @Nested
    class JavaVersionTest {
        @Test
        @EnabledOnJre(JRE.JAVA_8)
        void debeEjecutarseSoloEnJava8() {
        }

        @Test
        @EnabledOnJre(JRE.JAVA_11)
        void debeEjecutarseSoloEnJava11() {
        }
    }

    @Nested
    class SystemEnvironmentProperties {
        @Test
        void imprimirSystemProperties() {
            Properties properties = System.getProperties();
            properties.forEach((key, value) -> {
                System.out.println(key + ":" + value);
            });
        }

        @Test
        void imprimirVariablesDeAmbiente() {
            Map<String, String> getenv = System.getenv();
            getenv.forEach((k, v) -> {
                System.out.println(k + " = " + v);
            });
        }

        @Test
        @EnabledIfEnvironmentVariable(named = "USERDNSDOMAIN", matches = "CEIBA.LOCAL")
        void soloDebeEjecutarSiUserDnsDomainEsCeibaLocal() {
        }
    }

    @Test
    void debeCrearUnSaldoCuentaSiElDomainEsCeiba() {
        boolean esCeiba = "CEIBA.LOCAL".equals(System.getProperty("USERDNSDOMAIN"));
        Double saldoEsperado = 123.45235;
        Double saldoReal = this.cuenta.getSaldo().doubleValue();
        //assumeTrue(esCeiba);
        assumingThat(esCeiba, () -> {
            assertEquals(saldoEsperado, saldoReal);
        });
        assertFalse(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) < 0);
    }

    @RepeatedTest(value = 5, name = "Repeticion numero {currentRepetition} de {totalRepetitions}")
    void debeCrearUnCreditoCuentaRepetir5Veces(RepetitionInfo info) {
        if (info.getCurrentRepetition() == 3) {
            System.out.println("Repeticion " + info.getCurrentRepetition());
        }
        this.cuenta.credito(new BigDecimal(100));
        assertNotNull(this.cuenta.getSaldo());
        assertEquals(223, this.cuenta.getSaldo().intValue());
        assertEquals("223.45235", this.cuenta.getSaldo().toPlainString());
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @ValueSource(strings = {"10", "20", "30", "50", "70", "100"})
    void debeCrearUnDebitoCuentaValueSource(String monto) {
        this.cuenta.debito(new BigDecimal(monto));
        assertNotNull(this.cuenta.getSaldo());
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @CsvSource({"1,10", "2,20", "3,30", "4,50", "5,70", "6,100"})
    void debeCrearUnDebitoCuentaCsvSource(String index, String monto) {
        System.out.println(index + "=>" + monto);
        this.cuenta.debito(new BigDecimal(monto));
        assertNotNull(this.cuenta.getSaldo());
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @CsvSource({"20,10", "30,20", "40,30", "60,50", "90,70", "100,100"})
    void debeCrearUnDebitoCuentaCsvSource2(String saldo, String monto) {
        System.out.println(saldo + "=>" + monto);
        this.cuenta.debito(new BigDecimal(monto));
        assertNotNull(this.cuenta.getSaldo());
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @CsvFileSource(resources = "/data.csv")
    void debeCrearUnDebitoCuentaCsvFileSource(String monto) {
        System.out.println(monto);
        this.cuenta.debito(new BigDecimal(monto));
        assertNotNull(this.cuenta.getSaldo());
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    @ParameterizedTest(name = "numero {index} ejecutando con valor {argumentsWithNames}")
    @MethodSource("montoList")
    void debeCrearUnDebitoCuentaMethodSource(String monto) {
        System.out.println(monto);
        this.cuenta.debito(new BigDecimal(monto));
        assertNotNull(this.cuenta.getSaldo());
        assertTrue(this.cuenta.getSaldo().compareTo(BigDecimal.ZERO) > 0);
    }

    static List<String> montoList() {
        return Arrays.asList("10", "20", "30", "50", "90", "100");
    }

    @Tag("timeout")
    @Nested
    class TimeOutTest {
        @Test
        @Timeout(1)
        void pruebaTimeOut() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
        void pruebaTimeOut2() throws InterruptedException {
            TimeUnit.MILLISECONDS.sleep(900);
        }

        @Test
        void timeOutAssertionsTest() {
            assertTimeout(Duration.ofSeconds(1), () -> {
                TimeUnit.MILLISECONDS.sleep(900);
            });
        }
    }
}