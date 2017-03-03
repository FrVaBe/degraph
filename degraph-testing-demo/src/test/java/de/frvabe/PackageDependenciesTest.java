package de.frvabe;

import static de.schauderhaft.degraph.check.Check.classpath;
import static de.schauderhaft.degraph.check.JCheck.violationFree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import de.schauderhaft.degraph.configuration.NamedPattern;

public class PackageDependenciesTest {

    /**
     * Check if the packages of the module classpath are violation free (slices are implicit define
     * by the packages).
     */
    @Test(expected = AssertionError.class)
    public void allPackagesCheck() {
        assertThat(classpath().including("de.frvabe.**"), is(violationFree()));
    }

    /**
     * Check if the packages of the module are violation free (slices are explicit defined).
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
                        "de.frvabe.(*).**",
                        new NamedPattern("de.frvabe.*", "base"), 
                        new NamedPattern("de.frvabe.b.c.*", "c")),
                is(violationFree()));
    }

    /**
     * Check a dependency path between two packages that is considered to be valid.
     * <p>
     * Only the two relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test fail.
     * </p>
     */
    @Test
    public void validDependency() {
        assertThat(
                classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .withSlicing("packages",
                        "de.frvabe.(*).**",
                        new NamedPattern("de.frvabe.*", "base"),
                        new NamedPattern("de.frvabe.b.c.*", "c"))
                .allow("b", "a"),
                is(violationFree()));
    }

    /**
     * Check a dependency path between two packages that is considered to be valid (as it is the
     * opposite path of {@link #validDependency()} an exception is expected in this test.
     * <p>
     * Only the two relevant packages are included in the classpath because otherwise the invalid
     * package cycle of the overall project would make this test fail.
     * </p>
     */
    @Test(expected = AssertionError.class)
    public void invalidDependency() {
        assertThat(
                classpath()
                .including("de.frvabe.a.*")
                .including("de.frvabe.b.*")
                .withSlicing("packages",
                        "de.frvabe.(*).**",
                        new NamedPattern("de.frvabe.*", "base"),
                        new NamedPattern("de.frvabe.b.c.*", "c"))
                .allow("a", "b"),
                is(violationFree()));
    }

}
