package jp.co.drm.base.integration.mybatis.entity;

import java.io.Serializable;

public class People implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column people.person_id
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    private Long personId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column people.first_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    private String firstName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column people.last_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    private String lastName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table people
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column people.person_id
     *
     * @return the value of people.person_id
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public Long getPersonId() {
        return personId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column people.person_id
     *
     * @param personId the value for people.person_id
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column people.first_name
     *
     * @return the value of people.first_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column people.first_name
     *
     * @param firstName the value for people.first_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column people.last_name
     *
     * @return the value of people.last_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column people.last_name
     *
     * @param lastName the value for people.last_name
     *
     * @mbg.generated Wed Jul 12 14:08:25 JST 2017
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}