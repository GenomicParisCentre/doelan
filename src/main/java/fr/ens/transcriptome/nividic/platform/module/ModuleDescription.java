/*
 *                      Nividic development code
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  If you do not have a copy,
 * see:
 *
 *      http://www.gnu.org/copyleft/lesser.html
 *
 * Copyright for this code is held jointly by the microarray platform
 * of the École Normale Supérieure and the individual authors.
 * These should be listed in @author doc comments.
 *
 * For more information on the Nividic project and its aims,
 * or to join the Nividic mailing list, visit the home page
 * at:
 *
 *      http://www.transcriptome.ens.fr/nividic
 *
 */

package fr.ens.transcriptome.nividic.platform.module;

import java.util.Date;

import fr.ens.transcriptome.nividic.platform.PlatformException;
import fr.ens.transcriptome.nividic.util.Version;

/**
 * This class describe information about a module. By default the
 * moduleDescription object describe a module in unstable state in version
 * 0.0.1under the General Public Licence. this module as no dependancy and had
 * no requierement about any version of Nividic.
 * @author Laurent Jourdren
 */
public final class ModuleDescription implements AboutModule {

  private String moduleName;
  private Version version = new Version(0, 0, 1);
  private String organisation;
  private String[] authors;
  private String email;
  private String website;
  private String shortDescription;
  private String longDescription;
  private int stability = STATE_UNSTABLE;
  private ModuleDependancy[] dependancies;
  private Version minimalNividicVersion = new Version();
  private String licence;
  private String copyright;
  private Date versionDate;
  private String[] bibliography;
  private String htmlDocumentation;

  //
  // Getters
  //

  /**
   * Get the authors of the module.
   * @return The authors of the module
   */
  public String[] getAuthors() {
    return authors == null ? null : authors.clone();
  }

  /**
   * Get the dependancies of the module.
   * @return The dependancies of the module
   */
  public ModuleDependancy[] getDependancies() {
    return dependancies == null ? null : dependancies.clone();
  }

  /**
   * Get the email to contact authors of the module
   * @return The email to contact the authors of the module
   */
  public String getEmail() {
    return email;
  }

  /**
   * Get a long description of the module.
   * @return A long description of the module
   */
  public String getLongDescription() {
    return longDescription;
  }

  /**
   * Get the minimal version of Nividic needed to run the module.
   * @return The minimal version of Nividic needed to run the module
   */
  public Version getMinimalNividicVersion() {
    return minimalNividicVersion;
  }

  /**
   * Get the module name.
   * @return The name of the module
   */
  public String getName() {
    return moduleName;
  }

  /**
   * Get the name of the organisation of the authors of the module.
   * @return The name of the organisation of the authors of the module
   */
  public String getOrganisation() {
    return organisation;
  }

  /**
   * Get a short description of the module.
   * @return A short description of the module.
   */
  public String getShortDescription() {
    return shortDescription;
  }

  /**
   * Get the state of stability of the module.
   * @return the state of stability of the module
   */
  public int getStability() {
    return stability;
  }

  /**
   * Get the version of the module.
   * @return The version of the module
   */
  public Version getVersion() {
    return version;
  }

  /**
   * Get the website of the module.
   * @return The website of the module
   */
  public String getWebsite() {
    return website;
  }

  /**
   * Get the licence of the module.
   * @return The licence of the module
   */
  public String getLicence() {
    return licence;
  }

  /**
   * Get copyright of the module.
   * @return The copyright of the module
   */
  public String getCopyright() {
    return copyright;
  }

  /**
   * Get the date of the version of the module.
   * @return The date of the version of the module
   */
  public Date getVersionDate() {
    return (Date) versionDate.clone();
  }

  /**
   * Get the bibliography about the module.
   * @return The bibliography about the module
   */
  public String[] getBibliography() {
    return bibliography == null ? null : bibliography.clone();
  }

  /**
   * Get the HTML documentation of the module.
   * @return The HTML documentation of the module
   */
  public String getHTMLDocumentation() {
    return htmlDocumentation;
  }

  //
  // Setters
  //

  /**
   * Set the names of the authors.
   * @param names the names of the authors
   */
  public void setAuthors(final String[] names) {
    authors = names == null ? null : names.clone();
  }

  /**
   * Set the dependancies of the module.
   * @param dependancies The dependancies to set
   */
  public void setDependancies(final ModuleDependancy[] dependancies) {
    this.dependancies = dependancies == null ? null : dependancies.clone();
  }

  /**
   * Set email to contact the authors of the module.
   * @param email Email to contact the authors of the module
   */
  public void setEmail(final String email) {
    this.email = email;
  }

  /**
   * Set the long description of the module.
   * @param description The description of the module
   */
  public void setLongDescription(final String description) {
    longDescription = description;
  }

  /**
   * Set the minimal version of Nividic needed by the module.
   * @param version The version to set
   * @throws PlatformException if the version is null
   */
  public void setMinimalNividicVersion(final Version version)
      throws PlatformException {
    if (version == null)
      throw new PlatformException("the version of a module can't be null");
    minimalNividicVersion = version;
  }

  /**
   * Set the name of the module.
   * @param name The name of the module
   * @throws PlatformException if the name is null or empty
   */
  public void setName(final String name) throws PlatformException {
    if (name == null || name.equals(""))
      throw new PlatformException("the name of a module can't be null or empty");
    moduleName = name;
  }

  /**
   * Set the organisation of the authors of the module.
   * @param organisation The organisation of the authors of the module
   */
  public void setOrganisation(final String organisation) {
    this.organisation = organisation;
  }

  /**
   * Set the short description of the module.
   * @param description The description of the module
   * @throws PlatformException if the descritpion is null or empty
   */
  public void setShortDescription(final String description)
      throws PlatformException {
    if (description == null || description.equals(""))
      throw new PlatformException(
          "the shor description of a module can't be null or empty");
    shortDescription = description;
  }

  /**
   * Set the state of stability of the module.
   * @param state state of stability of the module
   * @throws PlatformException if the state of stability doesn't exists
   */
  public void setStability(final int state) throws PlatformException {
    if (state < AboutModule.STATE_STABLE
        || state > AboutModule.STATE_EXPERIMENTAL)
      throw new PlatformException(
          "this module state of stability doesn't exist");
    stability = state;
  }

  /**
   * Set the version of the module.
   * @param version The version of the module
   * @throws PlatformException if the version is null
   */
  public void setVersion(final Version version) throws PlatformException {
    if (version == null)
      throw new PlatformException("the version of a module can't be null");
    this.version = version;
  }

  /**
   * Set the url of the website of the module.
   * @param url of the module website
   */
  public void setWebsite(final String url) {
    website = url;
  }

  /**
   * Set the bibliography about the module.
   * @param bibliography The bibliography about the module
   */
  public void setBibliography(final String[] bibliography) {
    this.bibliography = bibliography == null ? null : bibliography.clone();
  }

  /**
   * Set the copyright of the module.
   * @param copyright The copyright of the module
   */
  public void setCopyright(final String copyright) {
    this.copyright = copyright;
  }

  /**
   * Set the licence of the module
   * @param licence The license to set
   */
  public void setLicence(final String licence) {
    this.licence = licence;
  }

  /**
   * Set the date of the version of the module.
   * @param date The date of the version of the module
   */
  public void setVersionDate(final Date date) {
    versionDate = date == null ? null : (Date) date.clone();
  }

  /**
   * Set the HTML documentation of the module.
   * @param htmlDocumentation The HTML documentation of the module
   */
  public void setHTMLDocumentation(final String htmlDocumentation) {
    this.htmlDocumentation = htmlDocumentation;
  }

  //
  // Constructor
  //

  /**
   * Public constructor.
   * @param moduleName the name of the module
   * @param shortDescription A short description of the module
   * @throws PlatformException if the parameters are incorrects
   */
  public ModuleDescription(final String moduleName,
      final String shortDescription) throws PlatformException {
    setName(moduleName);
    setShortDescription(shortDescription);
  }

}
