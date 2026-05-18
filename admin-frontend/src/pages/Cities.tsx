import { useEffect, useState } from 'react'
import { getCities, createCity, updateCity, deleteCity, City } from '../api/client'

export default function Cities() {
  const [cities, setCities] = useState<City[]>([])
  const [editCity, setEditCity] = useState<City | null>(null)
  const [editName, setEditName] = useState('')
  const [showDialog, setShowDialog] = useState(false)

  useEffect(() => { getCities().then(setCities) }, [])

  const openCreate = () => {
    setEditCity(null)
    setEditName('')
    setShowDialog(true)
  }

  const openEdit = (city: City) => {
    setEditCity(city)
    setEditName(city.name)
    setShowDialog(true)
  }

  const handleSave = async () => {
    if (!editName.trim()) return
    if (editCity) {
      await updateCity(editCity.id, editName)
    } else {
      await createCity(editName)
    }
    setShowDialog(false)
    getCities().then(setCities)
  }

  const handleDelete = async (id: string, name: string) => {
    if (!confirm(`Удалить город «${name}»?`)) return
    await deleteCity(id)
    getCities().then(setCities)
  }

  return (
    <div>
      <div className="page-header">
        <h1>Города</h1>
        <button className="btn btn-primary" style={{ width: 'auto' }} onClick={openCreate}>+ Добавить город</button>
      </div>

      <table>
        <thead>
          <tr><th>Название</th><th>Действия</th></tr>
        </thead>
        <tbody>
          {cities.map(c => (
            <tr key={c.id}>
              <td>{c.name}</td>
              <td>
                <button className="btn btn-sm btn-primary" onClick={() => openEdit(c)} style={{ marginRight: '0.5rem' }}>Редактировать</button>
                <button className="btn btn-sm btn-danger" onClick={() => handleDelete(c.id, c.name)}>Удалить</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {showDialog && (
        <div className="dialog-overlay" onClick={() => setShowDialog(false)}>
          <div className="dialog" onClick={e => e.stopPropagation()}>
            <h3>{editCity ? 'Редактировать город' : 'Добавить город'}</h3>
            <div className="form-group">
              <label>Название города</label>
              <input value={editName} onChange={e => setEditName(e.target.value)} autoFocus />
            </div>
            <div className="dialog-actions">
              <button className="btn btn-sm" onClick={() => setShowDialog(false)}>Отмена</button>
              <button className="btn btn-sm btn-primary" onClick={handleSave}>Сохранить</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
