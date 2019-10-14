package de.jwiegmann.twttr.infrastructure.domain.builder;

import javax.validation.ConstraintViolation;
import java.util.Iterator;
import java.util.Set;

import static org.apache.commons.lang3.Validate.notNull;

/**
 * Constraint Violation Format. Provides bean validation messages as concatenated string.
 */
final class ConstraintViolationFormat {

    private ConstraintViolationFormat() {
        super();
    }

    public static <E> String format(final Set<ConstraintViolation<E>> constraintViolations) {
        notNull(constraintViolations, "constraintViolations must not be null");

        Iterator<ConstraintViolation<E>> iterator = constraintViolations.iterator();

        StringBuilder sb = new StringBuilder();
        while (iterator.hasNext()) {
            ConstraintViolation<E> constraintViolation = iterator.next();
            sb.append(constraintViolation.getRootBeanClass().getSimpleName()).append('.')
                    .append(constraintViolation.getPropertyPath().toString()).append(' ')
                    .append(constraintViolation.getMessage());

            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }
}
