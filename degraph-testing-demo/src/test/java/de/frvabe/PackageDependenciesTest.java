package de.frvabe;

import static de.schauderhaft.degraph.check.Check.classpath;
import static de.schauderhaft.degraph.check.JCheck.violationFree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import de.schauderhaft.degraph.configuration.NamedPattern;

public class PackageDependenciesTest {

    /**
     * Checks if the packages of the module classpath are violation free (slices are implicit define
     * by the packages).
     */
    @Test(expected = AssertionError.class)
    public void allPackagesCheck() {
        assertThat(classpath().including("de.frvabe.**"), is(violationFree()));
    }

    /**
     * Checks if the packages of the module are violation free (slices are explicit defined).
     * <p>
     * Disclaimer: I am not sure of the implicit package slices still exists even if explicit
     * slicing is used.
     * </p>
     */
    @Test(expected = AssertionError.class)
    public void cycleFreePackageDepedencies() {
        assertThat(classpath()
                .including("de.frvabe.**")
                .withSlicing("packages",
                        new NamedPattern("de.frvabe.*", "base"),
                        "de.frvabe.(*).*",
                        "de.frvabe.b.(*).*"),
                is(violationFree()));
    }

    /**
     * Checks a dependency path between two packages that is considered to be valid.
     * <p>
     * Only the two relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test fail.
     * </p>
     */
    @Test
    public void validDependency() {
        assertThat(classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .withSlicing("packages",
                        "de.frvabe.(*).*")
                // dependency from left to right is OK; from right to left is not OK
                .allow("b", "a"),
                is(violationFree()));
    }

    /**
     * Checks a dependency path between two packages that is considered to be valid (as it is the
     * opposite path of {@link #validDependency()} an exception is expected in this test.
     * <p>
     * Only the two relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test fail.
     * </p>
     */
    @Test(expected = AssertionError.class)
    public void invalidDependency() {
        assertThat(classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .withSlicing("packages",
                        "de.frvabe.(*).*")
                // dependency from left to right is OK; from right to left is not OK
                .allow("a", "b"),
                is(violationFree()));
    }
    
    /**
     * Checks a dependency path between several packages that is considered to be valid.
     * <p>
     * Only the relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test fail.
     * </p>
     */
    @Test
    public void dependencyChain() {
        assertThat(classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .including("de.frvabe.b.c.*")
                .withSlicing("packages",
                        "de.frvabe.(*).*",
                        "de.frvabe.b.(*).*")
                // dependencies from left to right are OK; from right to left are not OK
                .allow("c", "b", "a"),
                is(violationFree()));
    }

    /**
     * Checks a strict dependency path between several packages. As {@code class C2} depends on
     * {@code class A1} while skipping the {@code package b} an exception is expected.
     * <p>
     * Only the two relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test also fail.
     * </p>
     */
    @Test(expected = AssertionError.class)
    public void strictDependencyChain() {
        assertThat(classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .including("de.frvabe.b.c.*")
                .withSlicing("packages",
                        "de.frvabe.(*).*",
                        "de.frvabe.b.(*).*")
                // c is allowed to depend on b (to skip a layer is not allowed)
                // b is allowed to depend on a
                .allowDirect("c", "b", "a"),
                is(violationFree()));
    }

}
