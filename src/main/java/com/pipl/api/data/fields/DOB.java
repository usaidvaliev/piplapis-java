package com.pipl.api.data.fields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Date-of-birth of A person.
 *
 * Comes as a date-range (the exact date is within the range, if the exact date
 * is known the range will simply be with start=end).
 */
public class DOB extends AbstractField {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("date_range")
	public DateRange dateDange;
	@Expose
	public String display;
	
	public DOB() {
	}
	
	/**
	 * @param dateRange the date-of-birth is within this range.
	 */
	public DOB(DateRange dateRange) {
		setDateRange(dateRange);
	}

	public DateRange getDateRange() {
		return dateDange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateDange = dateRange;
	}

	public boolean isSearchable() {
		return dateDange != null;
	}

	@Override
	public String toString() {
		if (display!=null)
			return display;
		return String.valueOf(age());
	}

	/**
	 * The estimated age of the person.
	 *
	 * Note that A DOB object is based on a date-range and the exact date is
	 * usually unknown so for age calculation the the middle of the range is
	 * assumed to be the real date-of-birth.
	 * @return age
	 */
	public int age() {
		Calendar dob = Calendar.getInstance();
		dob.setTime(dateDange.middle());
		Calendar now = Calendar.getInstance();
		if (dob.after(now)) {
			throw new IllegalArgumentException("Can't be born in the future");
		}
		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		int age = year1 - year2;
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		if (month2 > month1) {
			age--;
		} else if (month1 == month2) {
			int day1 = now.get(Calendar.DAY_OF_MONTH);
			int day2 = dob.get(Calendar.DAY_OF_MONTH);
			if (day2 > day1) {
				age--;
			}
		}
		return age;
	}

	/**
	 * A tuple of two ints - the minimum and maximum age of the person.
	 * @return <code>Tuple</code> object - example : (10, 55)
	 */
	public ArrayList<Integer> ageRange() {
		if (dateDange == null) {
			return new ArrayList<Integer>(Arrays.asList(null, null, 1));
		} else {
			DateRange end = new DateRange(dateDange.getStart(),
					dateDange.getStart());
			DateRange start = new DateRange(dateDange.getEnd(),
					dateDange.getEnd());
			return new ArrayList<Integer>(Arrays.asList(
					new DOB(start).age(), new DOB(end).age()));
		}
	}

	/**
	 * Take a person's birth year (int) and return a new DOB object suitable for him.
	 * @param birthYear birthYear
	 * @return <code>DOB</code> object
	 */
	public static DOB fromBirthYear(int birthYear) {
		if (birthYear < 0) {
			throw new IllegalArgumentException("birth year must be positive");
		}
		return new DOB(DateRange.fromYearsRange(birthYear, birthYear));
	}

	/**
	 * Take a person's birth date <code>Date</code> and return a new DOB object suitable for him.
	 * @param birthDate <code>Date</code> object
	 * @return <code>DOB</code> object
	 */
	public static DOB fromBirthDate(Date birthDate) {
		if (birthDate.getTime() > new Date().getTime()) {
			throw new IllegalArgumentException(
					"birth_date can't be in the future");
		}
		return new DOB(new DateRange(birthDate, birthDate));
	}

	/**
	 * Take a person's age (int) and return a new DOB object suitable for him.
	 * @param age age
	 * @return <code>DOB</code> object
	 */
	public static DOB fromAge(int age) {
		return DOB.fromAgeRange(age, age);
	}

	/**
	 * Take a person's minimal and maximal age and return a new DOB object
	 * suitable for him.
	 * @param start minimum age
	 * @param end maximum age
	 * @return <code>DOB</code> object
	 */
	public static DOB fromAgeRange(int start, int end) {
		if (start < 0 || end < 0) {
			throw new IllegalArgumentException(
					"start age and end age can't be negative");
		}
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
		}
		Calendar startDate = Calendar.getInstance();
		Calendar endDate = Calendar.getInstance();
		startDate.add(Calendar.YEAR, -end);
		endDate.add(Calendar.YEAR, -start);
		return new DOB(new DateRange(startDate.getTime(), endDate.getTime()));
	}

	public String getDisplay() {
		return display;
	}
}